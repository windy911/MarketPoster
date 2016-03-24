package com.rambo.marketposter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.rambo.marketposter.R;
import com.squareup.picasso.Picasso;

import org.xutils.view.annotation.ViewInject;


/**
 * Created by windy on 15/8/20.
 */
public class SplashActiivty extends BaseActivity {


    @ViewInject(R.id.imgSplash)
    public ImageView imgSplash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash);

        handler.sendEmptyMessageDelayed(0, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Picasso.with(this).load("http://a.hiphotos.baidu.com/news/q%3D100/sign=a16e08fd31d3d539c73d0bc30a86e927/e61190ef76c6a7efa69f8025fafaaf51f3de6673.jpg").into(imgSplash);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            go2MainActivity();
        }
    };

    private void go2MainActivity() {
        Intent intent = new Intent(SplashActiivty.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
