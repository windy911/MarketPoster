package com.rambo.marketposter.application;

import android.provider.Settings;

import com.rambo.marketposter.data.bean.Dish;
import com.rambo.marketposter.utils.JsonUtil;

import java.util.ArrayList;

/**
 * Created by windy on 16/4/14.
 * App 业务相关的工具
 */
public class AppUtils {

    //获取下单用的菜单Json String
    public static String getOrderDishJsonString(ArrayList<Dish> dishList) {

        if (dishList != null && dishList.size() > 0) {
            return JsonUtil.dishListToJsonString(dishList);
        }
        return "";

    }


    public static String getDeivcesID() {
        String android_id = Settings.Secure.getString(MyApplication.getAppContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }
}
