package com.rambo.marketposter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.rambo.marketposter.R;
import com.rambo.marketposter.activity.Base.BaseActivity;
import com.rambo.marketposter.application.ApiConst;
import com.rambo.marketposter.application.Constants;
import com.rambo.marketposter.data.feeds.OrderResultFeed;
import com.rambo.marketposter.network.response.OrderResultResponse;
import com.rambo.marketposter.utils.ActivityUtils;
import com.rambo.marketposter.utils.ToastUtil;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * Created by windy on 16/4/6.
 * 扫码支付 ＋ 支付成功 页
 */
public class PaySuccessActivity extends BaseActivity {

    public static final int HANDLE_MSG_GET_DATA_SUCCESS = 1;

    public static String BUNDEL_PAY_IMAGE_URL = "bundle_pay_image_url";


    @ViewInject(R.id.imgPayCode)
    ImageView imgPayCode;

    @ViewInject(R.id.tvAdHotLine)
    TextView tvAdHotLine;
    @ViewInject(R.id.tvAdAddress)
    TextView tvAdAddress;

    @ViewInject(R.id.btnIPayed)
    Button btnIPayed;

    @ViewInject(R.id.rlPayResultPayed)
    RelativeLayout rlPayResultPayed;
    @ViewInject(R.id.rlPayResultPaying)
    RelativeLayout rlPayResultPaying;
    @ViewInject(R.id.tvJumpingSeconds)
    private TextView tvJumpingSeconds;


    private long TIME_LIMIT = 10000;
    private CountDownTimer timer;

    public OrderResultFeed orderResultFeed;

    private String payImageUrl;

    private boolean isActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_success);
        ViewUtils.inject(this);
        initView();
        initBottomAD();
        isActive = true;
    }

    private void initView() {
        payImageUrl = getIntent().getStringExtra(BUNDEL_PAY_IMAGE_URL);
        Picasso.with(PaySuccessActivity.this).load(payImageUrl).placeholder(R.drawable.solid_gray).error(R.drawable.solid_gray).into(imgPayCode);
    }


    @OnClick({R.id.btnIPayed, R.id.llExit, R.id.llExit2})
    public void OnClicked(View view) {
        switch (view.getId()) {
            case R.id.btnIPayed:
                requestCheckOrder();
                break;
            case R.id.llExit:
            case R.id.llExit2:
                finishPage();
                break;
        }
    }


    public void requestCheckOrder() {
        btnIPayed.setEnabled(false);
        final String url = ApiConst.API_SERVER_IP + ApiConst.API_CHECK_ORDER;

        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", String.valueOf(OrderConfirmActivity.orderInfo.orderId));

        httpService.postStringRequest(url, map, new Response.Listener<String>() {
            @Override
            public void onResponse(String resp) {
                OrderResultResponse rs = new OrderResultResponse();
                orderResultFeed = new OrderResultFeed();
                rs.parseResponse(orderResultFeed, resp);
                myHandler.sendEmptyMessage(HANDLE_MSG_GET_DATA_SUCCESS);
                btnIPayed.setEnabled(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                btnIPayed.setEnabled(true);
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        isActive = false;
    }

    public void startTimer() {
        timer = new CountDownTimer(TIME_LIMIT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                setText(millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                if (isActive) {
                    setText("");
                    finishPage();
                }
            }
        };
        timer.start();
    }

    private void setText(String str) {
        tvJumpingSeconds.setText(str);
    }

    private void finishPage() {
        Intent intent = new Intent(PaySuccessActivity.this, MainGridActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityUtils.startActivity(PaySuccessActivity.this, intent);
    }


    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case HANDLE_MSG_GET_DATA_SUCCESS:
                    if (orderResultFeed.orderResult.isPayed()) {
                        rlPayResultPayed.setVisibility(View.VISIBLE);
                        rlPayResultPaying.setVisibility(View.GONE);
                        startTimer();
                    } else {
                        ToastUtil.show(PaySuccessActivity.this, "请扫描二维码完成付款");
                    }
                    break;

            }
        }
    };

    private void initBottomAD() {
        tvAdHotLine.setText(Constants.APP_NUMBER);
        tvAdAddress.setText(Constants.APP_CONFIG_URL);
    }
}
