package com.rambo.marketposter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.rambo.marketposter.activity.MainGridActivity;

/**
 * Created by windy on 16/4/19.
 */
public class BootUpReceiver extends BroadcastReceiver {
    Context context;

    public void onReceive(Context context, Intent intent) {


        this.context = context;

        handler.sendEmptyMessageDelayed(0, 7000);//5秒后启动
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Intent i = new Intent(context, MainGridActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

        }
    };


}