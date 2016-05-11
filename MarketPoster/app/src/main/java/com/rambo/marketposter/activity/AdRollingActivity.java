package com.rambo.marketposter.activity;

import android.os.Bundle;
import android.view.View;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.rambo.marketposter.R;
import com.rambo.marketposter.activity.Base.BaseActivity;
import com.rambo.marketposter.utils.Mylog;
import com.rambo.marketposter.widget.flashview.FlashView;
import com.rambo.marketposter.widget.flashview.constants.EffectConstants;
import com.rambo.marketposter.widget.flashview.listener.FlashViewListener;

import java.util.ArrayList;

/**
 * Created by windy on 16/4/6.
 * 广告轮播
 */
public class AdRollingActivity extends BaseActivity {


    @ViewInject(R.id.flashView)
    FlashView flashView;

    ArrayList<String> imageUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_rolling);
        ViewUtils.inject(this);
        initView();
    }

    private void initView() {
        imageUrls = new ArrayList<String>();
//        imageUrls.add("http://pic.to8to.com/attch/day_160218/20160218_d968438a2434b62ba59dH7q5KEzTS6OH.png");
//        imageUrls.add("http://pic.to8to.com/attch/day_160218/20160218_6410eaeeba9bc1b3e944xD5gKKhPEuEv.png");
//        imageUrls.add("http://img02.tooopen.com/images/20160216/tooopen_sy_156324542564.jpg");
//        imageUrls.add("http://www.52ij.com/uploads/allimg/160317/1110104P8-4.jpg");
//        imageUrls.add("http://pic.qiantucdn.com/58pic/19/43/68/56d3e7ffb7957_1024.jpg");
//        imageUrls.add("http://pic32.nipic.com/20130829/12906030_124355855000_2.png");

        if (MainGridActivity.mainGridFeed != null && MainGridActivity.mainGridFeed.mainGrid.advertises.size() > 0) {
            for (int i = 0; i < MainGridActivity.mainGridFeed.mainGrid.advertises.size(); i++) {
                String url = MainGridActivity.mainGridFeed.mainGrid.advertises.get(i).imgPath;
                imageUrls.add(url);
            }
        }

        flashView.setImageUris(imageUrls);
        flashView.setEffect(EffectConstants.DEFAULT_EFFECT);//更改图片切换的动画效果
        flashView.setDotsVisable(false);
        flashView.setOnPageClickListener(new FlashViewListener() {
            @Override
            public void onClick(int position) {
                Mylog.d("FlashView Clicked = ", position + 1 + "");
                finish();
            }
        });

    }

    @OnClick({R.id.tvTest0})
    public void OnClicked(View view) {
        switch (view.getId()) {
            case R.id.flashView:
                finish();
                break;
        }
    }

}
