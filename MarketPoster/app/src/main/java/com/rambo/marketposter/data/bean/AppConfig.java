package com.rambo.marketposter.data.bean;

/**
 * Created by windy on 16/4/12.
 */
public class AppConfig {
//    "phone":"400-8626-070",
//            "url":"http://www.yunyesoft.com",
//            "time":60000

    public String phone;
    public String url;
    public int time;

    public int getTime() {
        return Math.max(time, 10000);
    }
}
