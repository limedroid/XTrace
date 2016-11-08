package cn.droidlover.xtrace;


import android.os.Build;
import android.os.Looper;
import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.concurrent.TimeUnit;

/**
 * Created by wanglei on 2016/11/8.
 */

@Aspect
public class XTraceAspect {

    @Pointcut("execution(@cn.droidlover.xtrace.Trace * *(..))")
    public void method() {
    }

    @Around("method()")
    public Object execute(ProceedingJoinPoint point) throws Throwable {
        enterMethod(point);

        long startNanos = System.nanoTime();
        Object result = point.proceed();
        long stopNanos = System.nanoTime();

        long lengthMills = TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos);
        exitMethod(point, result, lengthMills);

        return result;
    }

    private static void enterMethod(ProceedingJoinPoint point) {
        CodeSignature codeSignature = (CodeSignature) point.getSignature();
        Class<?> clazz = codeSignature.getDeclaringType();
        String methodName = codeSignature.getName();
        String[] parameterNames = codeSignature.getParameterNames();
        Object[] parameterValues = point.getArgs();
        Trace trace = null;
        if (codeSignature instanceof MethodSignature) {
            trace = ((MethodSignature) codeSignature).getMethod().getAnnotation(Trace.class);
        }

        StringBuilder builder = new StringBuilder("\u21E2 ");
        if (trace != null) {
            builder.append(trace.value())
                    .append("->");
        }
        builder.append(methodName).append('(');
        for (int i = 0; i < parameterValues.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(parameterNames[i]).append("=");
            builder.append(parameterValues[i]);
        }
        builder.append(')');

        if (Looper.myLooper() != Looper.getMainLooper()) {
            builder.append(" [Thread:\"").append(Thread.currentThread().getName()).append("\"]");
        }

        android.util.Log.v(asTag(clazz), builder.toString());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final String section = builder.toString().substring(2);
            android.os.Trace.beginSection(section);
        }
    }

    private static void exitMethod(ProceedingJoinPoint point, Object result, long lengthMills) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            android.os.Trace.endSection();
        }

        Signature signature = point.getSignature();
        Class<?> clazz = signature.getDeclaringType();
        String methodName = signature.getName();
        boolean hasReturnType = signature instanceof MethodSignature
                && (((MethodSignature) signature).getReturnType() != Void.class);

        StringBuilder builder = new StringBuilder("\u21E0 ")
                .append(methodName)
                .append(" [")
                .append(lengthMills)
                .append("ms]");

        if (hasReturnType) {
            builder.append("=")
                    .append(result);
        }
        Log.v(asTag(clazz), builder.toString());
    }

    private static String asTag(Class<?> clazz) {
        if (clazz.isAnonymousClass()) {
            return asTag(clazz.getEnclosingClass());
        }
        return clazz.getSimpleName();
    }

}
