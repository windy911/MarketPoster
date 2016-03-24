package com.rambo.marketposter.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.rambo.marketposter.R;
import com.rambo.marketposter.utils.ToastUtil;

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
        ViewUtils.inject(this); //注入view和事件
        initView();
    }

    private void initView() {

    }

    @OnClick({R.id.tvTest0, R.id.tvTest1, R.id.tvTest2, R.id.tvTest3})
    public void OnViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvTest0:
                ToastUtil.show(MainActivity.this, "tvTest0");
                break;
            case R.id.tvTest1:
                ToastUtil.show(MainActivity.this, "tvTest1");
                break;
            case R.id.tvTest2:
                ToastUtil.show(MainActivity.this, "tvTest2");
                break;
            case R.id.tvTest3:
                ToastUtil.show(MainActivity.this, "tvTest3");
                break;
        }
    }


}
