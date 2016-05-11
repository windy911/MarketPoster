package com.rambo.marketposter.activity.Base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.rambo.marketposter.activity.AdRollingActivity;
import com.rambo.marketposter.application.Constants;
import com.rambo.marketposter.application.MyApplication;
import com.rambo.marketposter.network.HttpService;
import com.rambo.marketposter.utils.ActivityUtils;
import com.rambo.marketposter.utils.Mylog;
import com.rambo.marketposter.utils.ToastUtil;


/**
 * Created by Rambo on 15/8/19.
 */
public class BaseActivity extends Activity {

    public HttpService httpService;
    public long startActivityTime = System.currentTimeMillis();
    public boolean isResumed = false;

    public GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        httpService = new HttpService();
        Mylog.d("Current Activity: ", this.getClass().getSimpleName());

    }



    @Override
    protected void onResume() {
        super.onResume();
        handler.sendEmptyMessageDelayed(0, 1000);
        startActivityTime = System.currentTimeMillis();
        isResumed = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResumed = false;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        String name = this.getClass().getSimpleName();
        MyApplication.getAppContext().getRequestQueue().cancelAll(name);
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (isResumed) {
                    handler.sendEmptyMessageDelayed(0, 10000);
                    checkForAdRollingTime();
                }
            }
        }
    };

    public void checkForAdRollingTime() {
        if (this instanceof AdRollingActivity) {
            return;//广告轮播页面本身不会启动屏保逻辑。
        }


        if (System.currentTimeMillis() - startActivityTime > Constants.TIME_FOR_PAUSE_TO_AD) {
            Intent intent = new Intent(BaseActivity.this, AdRollingActivity.class);
            ActivityUtils.startActivity(this, intent);
            startActivityTime = System.currentTimeMillis();
        }
    }

    public void resetStartTimer() {
        Mylog.d("", "resetTimer");
        startActivityTime = System.currentTimeMillis();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        ToastUtil.show(this,"MotionEvent");
        Mylog.d("","dispatchTouchEvent resetStartTimer()");
        resetStartTimer();
        return super.dispatchTouchEvent(ev);
    }
}
