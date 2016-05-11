package com.rambo.marketposter.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rambo.marketposter.R;
import com.rambo.marketposter.activity.Base.BaseActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * 发现跳转网页;
 *
 * @author Dell
 */
public class WebViewActivity extends BaseActivity implements OnClickListener {

    public static final String BUNDLR_URL = "Bunlde_URL";

    private WebView webview;
    private MyWebViewClient client = null;
    private TextView title_back_g;
    private View parentview;
    private LayoutInflater inflater;
    private LinearLayout loadLayout;
    private String url;
    Map<String, Object> map = new HashMap<String, Object>();// 拼接参数；

    Button btnToBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        inflater = (LayoutInflater) this
                .getSystemService(this.LAYOUT_INFLATER_SERVICE);
        parentview = inflater.inflate(R.layout.foundview, null);
        setContentView(parentview);


        url = getIntent().getStringExtra(BUNDLR_URL);
        Log.i("INFO", "WebViewActivity:" + url);
        client = new MyWebViewClient();
        // 设置可以解析JavaScript代码;

        webview = (WebView) findViewById(R.id.webview);
        btnToBack = (Button) findViewById(R.id.btnToBack);
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);

        webview.setWebViewClient(client);
        webview.loadUrl(url);

        btnToBack.setOnClickListener(this);

    }

    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(WebViewActivity.this, "网络异常,请检查网络状态!",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void intiLoad(View view) {

    }


    private class MyWebViewClient extends WebViewClient {

        // 在WebView中而不是系统默认浏览器中显示页面
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i("INFO", "URL" + url);
            Log.i("INFO", "截取数据:" + url.substring(0, url.indexOf(":")));

            view.loadUrl(url);


            return true;
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view,
                                                          String url) {
            // 非超链接(如Ajax)请求无法直接添加请求头，现拼接到url末尾,这里拼接一个imei作为示例
            return super.shouldInterceptRequest(view, url);

        }

        // 报错;
        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            // TODO Auto-generated method stub
            myHandler.sendEmptyMessage(1);
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        // 页面载入前调用
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            intiLoad(parentview);
            super.onPageStarted(view, url, favicon);
        }

        // 页面载入完成后调用
        @Override
        public void onPageFinished(WebView view, String url) {

            super.onPageFinished(view, url);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断是否可以返回操作
        if (webview.canGoBack() && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            webview.goBack();
            return true;
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btnToBack:// 返回 ；
                WebViewActivity.this.finish();
                break;

            default:
                break;
        }
    }

}
