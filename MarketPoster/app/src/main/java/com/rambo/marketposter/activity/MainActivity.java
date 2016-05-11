package com.rambo.marketposter.activity;

import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.rambo.marketposter.R;
import com.rambo.marketposter.activity.Base.BaseActivity;
import com.rambo.marketposter.activity.Dialog.CarAgeDialog;
import com.rambo.marketposter.application.ApiConst;
import com.rambo.marketposter.data.feeds.CityFeed;
import com.rambo.marketposter.network.response.CitysResponse;
import com.rambo.marketposter.utils.ToastUtil;
import com.rambo.marketposter.widget.DialogUtils;
import com.rambo.marketposter.widget.VcodeButton;

import java.util.HashMap;

public class MainActivity extends BaseActivity {

    @ViewInject(R.id.tvTest0)
    private TextView tvText0;
    @ViewInject(R.id.tvTest1)
    private TextView tvText1;
    @ViewInject(R.id.tvTest2)
    private TextView tvText2;
    @ViewInject(R.id.tvTest3)
    private TextView tvText3;

    @ViewInject(R.id.btnVcode)
    private VcodeButton btnVocde;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);
        initView();
    }

    private void initView() {

    }

    @OnClick({R.id.tvTest0, R.id.tvTest1, R.id.tvTest2, R.id.tvTest3, R.id.btnVcode})
    public void OnViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvTest0:
                ToastUtil.show(MainActivity.this, "tvTest0");
                DialogUtils.createDialog(MainActivity.this, "测试TITLE", "测试CONTENT", "以后再说", "现在就去", new DialogUtils.OnItemSelected() {
                    @Override
                    public int ItemSelected(int seleced) {
                        if (RESULT_CANCEL == seleced) {
                            ToastUtil.show(MainActivity.this, "以后再说");
                        } else if (RESULT_CONFIRM == seleced) {
                            ToastUtil.show(MainActivity.this, "现在就去");
                        }
                        return 0;
                    }
                });
                break;
            case R.id.tvTest1:
                ToastUtil.show(MainActivity.this, "tvTest1");
                openCarAgeDialog();
                break;
            case R.id.tvTest2:
                ToastUtil.show(MainActivity.this, "tvTest2");
                break;
            case R.id.tvTest3:
                ToastUtil.show(MainActivity.this, "tvTest3");
                break;
            case R.id.btnVcode:
                btnVocde.setEnabled(true);
                btnVocde.startTimer(60000);
                break;
        }
    }

    // 选择车龄dialog
    public void openCarAgeDialog() {
        CarAgeDialog dialog = new CarAgeDialog(this,
                new CarAgeDialog.OnSelectedDisplacementListener() {

                    @Override
                    public void onSelected(int index) {
                        // TODO Auto-generated method stub
                        ToastUtil.show(MainActivity.this, "Selected = " + index);
                    }
                }, 0);
        dialog.showDialog(0, 0);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); // 设置宽度
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setAttributes(lp);
    }


    public void requestPostData() {
        final String url = ApiConst.API_MAIN_GRID_PAGE;
        final HashMap<String, String> map = new HashMap<String, String>();

        map.put("platform", "Android");
        map.put("version", "1.0");

        httpService.postStringRequest(url, map, new Response.Listener<String>() {
            @Override
            public void onResponse(String resp) {
                CitysResponse rs = new CitysResponse();
                CityFeed cityFeed = new CityFeed();
                rs.parseResponse(cityFeed, resp);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

    public void requestGetData() {

        final String url = ApiConst.API_MAIN_GRID_PAGE;


        httpService.getStringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String resp) {
                CitysResponse rs = new CitysResponse();
                CityFeed cityFeed = new CityFeed();
                rs.parseResponse(cityFeed, resp);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }
}
