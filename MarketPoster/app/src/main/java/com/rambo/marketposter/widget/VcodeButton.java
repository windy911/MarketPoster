package com.rambo.marketposter.widget;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * 倒计时 60 秒
 */
public class VcodeButton extends Button {

    private long TIME_LIMIT = 60000;

    private CountDownTimer timer;

    public VcodeButton(Context context) {
        super(context);
    }

    public VcodeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VcodeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void startTimer(long timerLimit) {
        TIME_LIMIT = timerLimit;
        timer = new CountDownTimer(TIME_LIMIT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                setEnabled(false);
                setText(millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                setText("发送验证码");
                setEnabled(true);
            }
        };
        timer.start();
    }

}
