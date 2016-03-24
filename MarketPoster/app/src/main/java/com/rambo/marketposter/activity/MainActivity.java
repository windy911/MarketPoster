package com.rambo.marketposter.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.rambo.marketposter.R;

import org.xutils.view.annotation.ViewInject;

public class MainActivity extends Activity {

    @ViewInject(R.id.tvTest0)
    private TextView tvText0;
    @ViewInject(R.id.tvTest1)
    private TextView tvText1;
    @ViewInject(R.id.tvTest2)
    private TextView tvText2;
    @ViewInject(R.id.tvTest3)
    private TextView tvText3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

}
