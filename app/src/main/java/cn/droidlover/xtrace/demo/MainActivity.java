package cn.droidlover.xtrace.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.droidlover.xtrace.Trace;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sum(5, 10);
    }

    @Trace("听音乐")
    public void onMusic(View view) {
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Trace("看电影")
    public void onMovie(View view) {
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Trace("聊天")
    public void onChat(View view) {
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Trace("求和")
    private int sum(int a, int b) {
        return a + b;
    }


}
