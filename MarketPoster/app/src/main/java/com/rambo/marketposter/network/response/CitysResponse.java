package com.rambo.marketposter.network.response;

import com.rambo.marketposter.data.bean.City;
import com.rambo.marketposter.data.feeds.CityFeed;
import com.rambo.marketposter.data.feeds.Feed;
import com.rambo.marketposter.utils.JsonUtil;

import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by windy on 15/8/20.
 */
public class CitysResponse extends AbsBaseResponse {

    public CityFeed parseResponse(CityFeed feed, String resp) {
        super.parseBaseResponse(feed, resp);
        return parseLocalResponse(feed, resp);
    }

    private CityFeed parseLocalResponse(CityFeed feed, String resp) {
        try {
            JSONObject jsonObject = (JSONObject) new JSONTokener(resp).nextValue();
            JSONObject data = jsonObject.getJSONObject("data");
            City city = new City();
            city.cityCode = JsonUtil.getValueOfStr(data, "cityCode");
            city.cityName = JsonUtil.getValueOfStr(data, "cityName");
            feed.city = city;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return feed;
    }


}
