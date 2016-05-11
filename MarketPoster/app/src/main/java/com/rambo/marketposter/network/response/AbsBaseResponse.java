package com.rambo.marketposter.network.response;

import com.rambo.marketposter.data.feeds.Feed;
import com.rambo.marketposter.utils.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by windy on 15/8/20.
 */
public class AbsBaseResponse {

    protected Feed parseBaseResponse(Feed feed, String resp) {

        try {
            JSONObject json = (JSONObject) new JSONTokener(resp).nextValue();
            feed.resCode = JsonUtil.getValueOfInt(json, "code");
            feed.resMsg = JsonUtil.getValueOfStr(json, "message");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return feed;
    }
}
