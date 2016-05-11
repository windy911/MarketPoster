package com.rambo.marketposter.network.response;

import com.rambo.marketposter.data.feeds.OrderResultFeed;
import com.rambo.marketposter.utils.JsonUtil;

import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by windy on 15/8/20.
 */
public class OrderResultResponse extends AbsBaseResponse {

    public OrderResultFeed parseResponse(OrderResultFeed feed, String resp) {
        super.parseBaseResponse(feed, resp);
        return parseLocalResponse(feed, resp);
    }

    private OrderResultFeed parseLocalResponse(OrderResultFeed feed, String resp) {
        try {
            JSONObject jsonObject = (JSONObject) new JSONTokener(resp).nextValue();
            feed.orderResult.state = JsonUtil.getValueOfInt(jsonObject, "state");
            feed.orderResult.imgPath = JsonUtil.getValueOfStr(jsonObject, "imgPath");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return feed;
    }


}
