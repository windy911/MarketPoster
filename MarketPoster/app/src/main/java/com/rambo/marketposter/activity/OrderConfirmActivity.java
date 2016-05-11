package com.rambo.marketposter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.rambo.marketposter.R;
import com.rambo.marketposter.activity.Base.BaseActivity;
import com.rambo.marketposter.application.ApiConst;
import com.rambo.marketposter.application.AppUtils;
import com.rambo.marketposter.application.Constants;
import com.rambo.marketposter.data.bean.OrderInfo;
import com.rambo.marketposter.data.feeds.OrderResultFeed;
import com.rambo.marketposter.network.response.OrderResultResponse;
import com.rambo.marketposter.utils.ActivityUtils;
import com.rambo.marketposter.utils.StringUtil;
import com.rambo.marketposter.utils.ToastUtil;

import java.util.HashMap;

/**
 * Created by windy on 16/4/6.
 * 订单确认页
 */
public class OrderConfirmActivity extends BaseActivity {

    public static final int HANDLE_MSG_GET_DATA_SUCCESS = 1;
    public static final String BUNDLE_ORDER_INFO = "bundle_order_info";

    @ViewInject(R.id.tvOrderNum)
    TextView tvOrderNum;
    @ViewInject(R.id.tvOrderAmout)
    TextView tvOrderAmout;
    @ViewInject(R.id.tvOrderExpress)
    TextView tvOrderExpress;
    @ViewInject(R.id.tvOrderTotalAmount)
    TextView tvOrderTotalAmount;

    @ViewInject(R.id.edtOrderContactName)
    TextView edtOrderContactName;
    @ViewInject(R.id.edtOrderContactPhone)
    TextView edtOrderContactPhone;


    @ViewInject(R.id.btnToPay)
    Button btnToPay;
    @ViewInject(R.id.btnToBack)
    Button btnToBack;

    @ViewInject(R.id.llExit2)
    LinearLayout llExit2;

    @ViewInject(R.id.llOrderExpress)
    LinearLayout llOrderExpress;

    @ViewInject(R.id.llWeiXin)
    LinearLayout llWeiXin;
    @ViewInject(R.id.llZhiFuBao)
    LinearLayout llZhiFuBao;

    @ViewInject(R.id.cbWeiXin)
    CheckBox cbWeiXin;
    @ViewInject(R.id.cbZhiFuBao)
    CheckBox cbZhiFuBao;

    @ViewInject(R.id.tvAdHotLine)
    TextView tvAdHotLine;
    @ViewInject(R.id.tvAdAddress)
    TextView tvAdAddress;

    @ViewInject(R.id.llOrderUserNameInput)
    LinearLayout llOrderUserNameInput;


    private int orderId = 0;

    OrderResultFeed orderResultFeed;

    public static OrderInfo orderInfo;

    private int paySelelcted = 0;
    private int PAY_WEIXIN = 1;
    private int PAY_ZHIFUBAO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confrim);
        ViewUtils.inject(this);
//        ToastUtil.show(OrderConfirmActivity.this, "machineCode = " + AppUtils.getDeivcesID());
        initView();
        initBottomAD();
    }

    private void initView() {
        paySelelcted = 0;

        orderInfo = (OrderInfo) getIntent().getSerializableExtra(BUNDLE_ORDER_INFO);
        if (orderInfo != null) {
            orderId = orderInfo.orderId;
            tvOrderNum.setText(orderInfo.orderNum);
            tvOrderAmout.setText(orderInfo.orderAmount + "元");
            tvOrderExpress.setText(orderInfo.orderExpressFee + "元");
            tvOrderTotalAmount.setText(orderInfo.orderTotalAmount + "元");


            if (orderInfo.orderExpressFee == 0) {
                llOrderExpress.setVisibility(View.GONE);
            }
        }

        llOrderUserNameInput.setVisibility(View.GONE);
    }


    @OnClick({R.id.btnToPay, R.id.btnToBack, R.id.llExit2, R.id.llWeiXin, R.id.llZhiFuBao})
    public void OnClicked(View view) {
        switch (view.getId()) {
            case R.id.btnToPay:
                requestSaveOrder();
                break;
            case R.id.btnToBack:
                finish();
                break;
            case R.id.llExit2:
                finishPage();
                break;
            case R.id.llWeiXin:
                paySelected(1);
                break;
            case R.id.llZhiFuBao:
                paySelected(2);
                break;
        }
    }


    private String getPayMethod() {
        if (paySelelcted == PAY_WEIXIN) {
            return "微信";
        } else if (paySelelcted == PAY_ZHIFUBAO) {
            return "支付宝";
        } else return "";
    }

    private void paySelected(int payMethod) {
        paySelelcted = payMethod;
        if (payMethod == PAY_WEIXIN) {
            cbWeiXin.setChecked(true);
            cbZhiFuBao.setChecked(false);
        } else if (payMethod == PAY_ZHIFUBAO) {
            cbWeiXin.setChecked(false);
            cbZhiFuBao.setChecked(true);
        }
    }

    private void finishPage() {
        Intent intent = new Intent(OrderConfirmActivity.this, MainGridActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityUtils.startActivity(OrderConfirmActivity.this, intent);
    }

    //检查是否输入信息完整
    private boolean checkValid() {

        if (StringUtil.isEmpty(tvOrderNum.getText().toString().trim())) {
            ToastUtil.show(OrderConfirmActivity.this, "订单编号缺失");
            return false;
        }
        String phone = edtOrderContactPhone.getText().toString().trim();
        if (StringUtil.isEmpty(phone)) {
            ToastUtil.show(OrderConfirmActivity.this, "请输入联系人手机号码");
            return false;
        }

        if (!phone.startsWith("1") || phone.length() != 11) {
            ToastUtil.show(OrderConfirmActivity.this, "请输入正确的手机号码");
            return false;
        }

//        if (StringUtil.isEmpty(edtOrderContactName.getText().toString().trim())) {
//            ToastUtil.show(OrderConfirmActivity.this, "请输入联系人姓名");
//            return false;
//        }

        if (paySelelcted == 0) {
            ToastUtil.show(OrderConfirmActivity.this, "请选择支付方式");
            return false;
        }
        return true;
    }

    private void requestSaveOrder() {
        if (!checkValid()) return;

        final String url = ApiConst.API_SERVER_IP + ApiConst.API_SAVE_ORDER;

        final HashMap<String, String> map = new HashMap<String, String>();
//        id	是	int	订单编号
//        contact	是	string	联系人
//        phone	是	string	联系电话
//        paytype	是	string	支付方式
//        state	是	int	订单状态 0：待付款 1：已付款
        map.put("id", String.valueOf(orderId));
        map.put("contact", edtOrderContactName.getText().toString().trim());
        map.put("phone", edtOrderContactPhone.getText().toString().trim());
        map.put("payType", getPayMethod());
        map.put("machineCode", AppUtils.getDeivcesID());
        btnToPay.setEnabled(false);

        httpService.postStringRequest(url, map, new Response.Listener<String>() {
            @Override
            public void onResponse(String resp) {
                OrderResultResponse rs = new OrderResultResponse();
                orderResultFeed = new OrderResultFeed();
                rs.parseResponse(orderResultFeed, resp);
                myHandler.sendEmptyMessage(HANDLE_MSG_GET_DATA_SUCCESS);
                btnToPay.setEnabled(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                btnToPay.setEnabled(true);
            }
        });

    }


    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case HANDLE_MSG_GET_DATA_SUCCESS:
                    if (orderResultFeed.resCode == 0) {
                        Intent intent = new Intent(OrderConfirmActivity.this, PaySuccessActivity.class);
                        intent.putExtra(PaySuccessActivity.BUNDEL_PAY_IMAGE_URL, orderResultFeed.orderResult.imgPath);
                        ActivityUtils.startActivity(OrderConfirmActivity.this, intent);
                    } else {
                        ToastUtil.show(OrderConfirmActivity.this, orderResultFeed.resMsg);
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
