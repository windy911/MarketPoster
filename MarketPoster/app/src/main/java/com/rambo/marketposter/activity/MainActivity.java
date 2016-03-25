package com.rambo.marketposter.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.rambo.marketposter.R;
import com.rambo.marketposter.utils.ToastUtil;
import com.rambo.marketposter.widget.DialogUtils;

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
                DialogUtils.createDialog(MainActivity.this, "测试TITLE", "测试CONTENT", "以后再说", "现在就去", new DialogUtils.OnItemSelected() {
                    @Override
                    public int ItemSelected(int seleced) {
                        if(RESULT_CANCEL == seleced){
                            ToastUtil.show(MainActivity.this,"以后再说");
                        }else if(RESULT_CONFIRM == seleced){
                            ToastUtil.show(MainActivity.this,"现在就去");
                        }
                        return 0;
                    }
                });
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
