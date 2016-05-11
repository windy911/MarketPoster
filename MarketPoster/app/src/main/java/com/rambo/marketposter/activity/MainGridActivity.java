package com.rambo.marketposter.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
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
import com.rambo.marketposter.data.bean.MainGridItem;
import com.rambo.marketposter.data.feeds.MainGridFeed;
import com.rambo.marketposter.network.response.GridMainResponse;
import com.rambo.marketposter.utils.ActivityUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by windy on 16/4/6.
 * 六选一首页
 */
public class MainGridActivity extends BaseActivity {

    public static final int HANDLE_MSG_GET_DATA_SUCCESS = 1;
    public static final int HANDLE_MSG_GET_DATA_FAILED = 2;


    public static MainGridFeed mainGridFeed;
    private ArrayList<MainGridItem> gridItems;
    private MainGridAdapter mainGridAdapter;


    @ViewInject(R.id.gridView)
    GridView gridView;

    @ViewInject(R.id.tvAdHotLine)
    TextView tvHotLine;

    @ViewInject(R.id.tvAdHotLine)
    TextView tvAdHotLine;
    @ViewInject(R.id.tvAdAddress)
    TextView tvAdAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maingrid);
        ViewUtils.inject(this);


        initScreen();


    }

    @Override
    protected void onResume() {
        super.onResume();
        requestGetData();
    }

    private void initScreen() {
        // 方法1 Android获得屏幕的宽和高
//        WindowManager windowManager = getWindowManager();
//        Display display = windowManager.getDefaultDisplay();
//        int screenWidth = screenWidth = display.getWidth();
//        int screenHeight = screenHeight = display.getHeight();

//        tvHotLine.setText("Screen Info : " + screenWidth + " * " + screenHeight);
    }

    private void initView() {

        Constants.TIME_FOR_PAUSE_TO_AD = mainGridFeed.mainGrid.appConfig.getTime();
        Constants.APP_NUMBER = mainGridFeed.mainGrid.appConfig.phone;
        Constants.APP_CONFIG_URL = mainGridFeed.mainGrid.appConfig.url;
        gridItems = mainGridFeed.mainGrid.mainGridItems;
        mainGridAdapter = new MainGridAdapter(MainGridActivity.this);
        gridView.setAdapter(mainGridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(MainGridActivity.this, ShopListActivity.class);
                    ActivityUtils.startActivity(MainGridActivity.this, intent);
                } else {
                    Intent intent2 = new Intent(MainGridActivity.this, WebViewActivity.class);
                    intent2.putExtra(WebViewActivity.BUNDLR_URL, gridItems.get(position).link);
                    ActivityUtils.startActivity(MainGridActivity.this, intent2);
                }
            }
        });

        initBottomAD();
    }


    @OnClick({R.id.tvTest0})
    public void OnClicked(View view) {
        switch (view.getId()) {
        }
    }

    public void requestGetData() {

        final String url = ApiConst.API_SERVER_IP + ApiConst.API_MAIN_GRID_PAGE;


        httpService.getStringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String resp) {
                GridMainResponse rs = new GridMainResponse();
                mainGridFeed = new MainGridFeed();
                rs.parseResponse(mainGridFeed, resp);
                myHandler.sendEmptyMessage(HANDLE_MSG_GET_DATA_SUCCESS);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == HANDLE_MSG_GET_DATA_SUCCESS) {
                initView();
            }
        }
    };


    class MainGridAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;

        public MainGridAdapter(Context context) {
            this.mContext = context;
            mInflater = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return gridItems != null ? gridItems.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return gridItems != null ? gridItems.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.grid_view_item, null);

                holder.imgGridItem = (ImageView) convertView
                        .findViewById(R.id.imgGridItem);
                holder.tvGridItem = (TextView) convertView
                        .findViewById(R.id.tvGridItem);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            MainGridItem item = gridItems.get(position);

            Picasso.with(MainGridActivity.this).load(item.imgPath).placeholder(R.drawable.solid_gray).error(R.drawable.solid_gray).into(holder.imgGridItem);
            holder.tvGridItem.setText(item.name);


            return convertView;
        }

        public class ViewHolder {
            public TextView tvGridItem;
            public ImageView imgGridItem;
        }
    }

    private void initBottomAD() {
        tvAdHotLine.setText(Constants.APP_NUMBER);
        tvAdAddress.setText(Constants.APP_CONFIG_URL);
    }
}
