package com.rambo.marketposter.network.response;

import com.rambo.marketposter.data.bean.Dish;
import com.rambo.marketposter.data.bean.DishType;
import com.rambo.marketposter.data.bean.OrderInfo;
import com.rambo.marketposter.data.feeds.DishListFeed;
import com.rambo.marketposter.data.feeds.OrderListFeed;
import com.rambo.marketposter.utils.JsonUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by windy on 15/8/20.
 */
public class OrderGenerateResponse extends AbsBaseResponse {

    public OrderListFeed parseResponse(OrderListFeed feed, String resp) {
        super.parseBaseResponse(feed, resp);
        return parseLocalResponse(feed, resp);
    }

    private OrderListFeed parseLocalResponse(OrderListFeed feed, String resp) {
        try {
            JSONObject jsonObject = (JSONObject) new JSONTokener(resp).nextValue();
            JSONArray data = JsonUtil.getValueOfJsonArray(jsonObject, "result");//所有的菜
            if (data != null && data.length() > 0) {
                for (int i = 0; i < data.length(); i++) {
                    JSONObject object = data.getJSONObject(i);
//                    "id":14,
//                            "orderNum":"20167",
//                            "orderAmount":21,
//                            "expressfee":0,
//                            "totalAmount":21,
//                            "area":null,
//                            "address":null,
//                            "contact":"李四",
//                            "phone":"15648725456",
//                            "receiveDate":null,
//                            "payType":null,
//                            "payTime":null,
//                            "state":0,
//                            "remark":null,
//                            "createTime":1460517432000,
//                            "orderProduceId":null,
                    OrderInfo orderInfo = new OrderInfo();
                    orderInfo.orderId = JsonUtil.getValueOfInt(object, "id");
                    orderInfo.orderNum = JsonUtil.getValueOfStr(object, "orderNum");
                    orderInfo.orderAmount = JsonUtil.getValueOfDouble(object, "orderAmount");
                    orderInfo.orderExpressFee = JsonUtil.getValueOfDouble(object, "expressfee");
                    orderInfo.orderTotalAmount = JsonUtil.getValueOfDouble(object, "totalAmount");
                    orderInfo.orderContact = JsonUtil.getValueOfStr(object, "contact");
                    orderInfo.orderPhone = JsonUtil.getValueOfStr(object, "phone");
                    orderInfo.orderPayType = JsonUtil.getValueOfStr(object, "payType");
                    orderInfo.orderState = JsonUtil.getValueOfInt(object, "state");
                    orderInfo.orderRemark = JsonUtil.getValueOfStr(object, "remark");
                    orderInfo.orderCreateTime = JsonUtil.getValueOfStr(object, "createTime");

                    feed.orderInfos.add(orderInfo);
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
        return feed;
    }


}
