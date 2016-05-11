package com.rambo.marketposter.network.response;

import com.rambo.marketposter.data.bean.Advertise;
import com.rambo.marketposter.data.bean.MainGridItem;
import com.rambo.marketposter.data.feeds.MainGridFeed;
import com.rambo.marketposter.utils.JsonUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by windy on 15/8/20.
 */
public class GridMainResponse extends AbsBaseResponse {

    public MainGridFeed parseResponse(MainGridFeed feed, String resp) {
        super.parseBaseResponse(feed, resp);
        return parseLocalResponse(feed, resp);
    }
    //广告热线  400-8626-070
    //http://www.yunyesoft.com

    private MainGridFeed parseLocalResponse(MainGridFeed feed, String resp) {
        try {
            JSONObject jsonObject = (JSONObject) new JSONTokener(resp).nextValue();
            JSONArray data = JsonUtil.getValueOfJsonArray(jsonObject, "appList");

            JSONObject configJson = JsonUtil.getValueOfObject(jsonObject, "appConfigDTO");
            feed.mainGrid.appConfig.phone = JsonUtil.getValueOfStr(configJson, "phone");
            feed.mainGrid.appConfig.url = JsonUtil.getValueOfStr(configJson, "url");
            feed.mainGrid.appConfig.time = JsonUtil.getValueOfInt(configJson, "time");

            if (data != null && data.length() > 0) {
                for (int i = 0; i < data.length(); i++) {
                    JSONObject objectAppList = data.getJSONObject(i);
//                    public int id;
//                    public String name;
//                    public String imgPath;
//                    public String link;
//                    public String type;
//                    public int state;
//                    public String align;
                    MainGridItem mainGridItem = new MainGridItem();
                    mainGridItem.id = JsonUtil.getValueOfInt(objectAppList, "id");
                    mainGridItem.state = JsonUtil.getValueOfInt(objectAppList, "state");
                    mainGridItem.name = JsonUtil.getValueOfStr(objectAppList, "name");
                    mainGridItem.imgPath = JsonUtil.getValueOfStr(objectAppList, "imgPath");
                    mainGridItem.link = JsonUtil.getValueOfStr(objectAppList, "link");
                    mainGridItem.type = JsonUtil.getValueOfStr(objectAppList, "type");
                    mainGridItem.align = JsonUtil.getValueOfStr(objectAppList, "align");

                    feed.mainGrid.mainGridItems.add(mainGridItem);
                }
            }


            JSONArray data2 = JsonUtil.getValueOfJsonArray(jsonObject, "advertiseDto");
            if (data2 != null && data2.length() > 0) {
                for (int i = 0; i < data2.length(); i++) {
                    JSONObject objectAdvertise = data2.getJSONObject(i);
//                    public String id;
//                    public String name;
//                    public String imgPath;
//                    public String link;
//                    public int state;
//                    public String type;
//                    public String align;
                    Advertise advertise = new Advertise();
                    advertise.id = JsonUtil.getValueOfInt(objectAdvertise, "id");
                    advertise.state = JsonUtil.getValueOfInt(objectAdvertise, "state");
                    advertise.name = JsonUtil.getValueOfStr(objectAdvertise, "name");
                    advertise.imgPath = JsonUtil.getValueOfStr(objectAdvertise, "imgPath");
                    advertise.link = JsonUtil.getValueOfStr(objectAdvertise, "link");
                    advertise.type = JsonUtil.getValueOfStr(objectAdvertise, "type");
                    advertise.align = JsonUtil.getValueOfStr(objectAdvertise, "align");
                    feed.mainGrid.advertises.add(advertise);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return feed;
    }


}
