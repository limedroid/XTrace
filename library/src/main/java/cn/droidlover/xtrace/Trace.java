package cn.droidlover.xtrace;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by wanglei on 2016/11/8.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface Trace {
    String value() default "";
}
