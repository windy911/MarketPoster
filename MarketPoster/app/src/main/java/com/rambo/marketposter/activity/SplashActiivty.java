package com.rambo.marketposter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.rambo.marketposter.R;
import com.rambo.marketposter.activity.Base.BaseActivity;
import com.squareup.picasso.Picasso;


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
        ViewUtils.inject(this);
        handler.sendEmptyMessageDelayed(0, 3000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Picasso.with(SplashActiivty.this).load("http://img6.cache.netease.com/photo/0001/2016-03-23/BISIUFT657KT0001.jpg").into(imgSplash);
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
