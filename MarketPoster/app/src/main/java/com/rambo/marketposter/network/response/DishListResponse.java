package com.rambo.marketposter.network.response;

import com.rambo.marketposter.data.bean.Dish;
import com.rambo.marketposter.data.bean.DishType;
import com.rambo.marketposter.data.feeds.DishListFeed;
import com.rambo.marketposter.utils.JsonUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by windy on 15/8/20.
 */
public class DishListResponse extends AbsBaseResponse {

    public DishListFeed parseResponse(DishListFeed feed, String resp) {
        super.parseBaseResponse(feed, resp);
        return parseLocalResponse(feed, resp);
    }

    private DishListFeed parseLocalResponse(DishListFeed feed, String resp) {
        try {
            JSONObject jsonObject = (JSONObject) new JSONTokener(resp).nextValue();
            JSONArray data = JsonUtil.getValueOfJsonArray(jsonObject, "normalList");//所有的菜
            if (data != null && data.length() > 0) {
                for (int i = 0; i < data.length(); i++) {
                    JSONObject object = data.getJSONObject(i);
//                    "id":2,
//                            "name":"青椒",
//                            "oprice":9,
//                            "cprice":4.5,
//                            "uprice":3,
//                            "specification":"3个/袋",
//                            "imagepath":"/imgs/produce/2.png",
//                            "type":"4",
//                            "produceTypeName":"蔬菜",
//                            "praisecount":3,
//                            "remark":null,
//                            "stock":5,
//                            "totalSold":1,
                    Dish dishItem = new Dish();
                    dishItem.dishId = JsonUtil.getValueOfInt(object, "id");
                    dishItem.dishName = JsonUtil.getValueOfStr(object, "name");
                    dishItem.dishOprice = JsonUtil.getValueOfDouble(object, "oPrice");
                    dishItem.dishCprice = JsonUtil.getValueOfDouble(object, "cPrice");
                    dishItem.dishUprice = JsonUtil.getValueOfDouble(object, "uPrice");
                    dishItem.dishSpecification = JsonUtil.getValueOfStr(object, "specification");
                    dishItem.dishPic = JsonUtil.getValueOfStr(object, "imagePath");
                    dishItem.dishType = JsonUtil.getValueOfInt(object, "type");
                    dishItem.dishProduceTypeName = JsonUtil.getValueOfStr(object, "produceTypeName");
                    dishItem.dishPraisecount = JsonUtil.getValueOfInt(object, "praiseCount");
                    dishItem.dishRemark = JsonUtil.getValueOfStr(object, "remark");
                    dishItem.dishStock = JsonUtil.getValueOfInt(object, "stock");
                    dishItem.dishTotalSold = JsonUtil.getValueOfInt(object, "totalSold");
                    dishItem.qrCodePath = JsonUtil.getValueOfStr(object, "qrCodePath");


                    feed.dishList.dishNormalItems.add(dishItem);
                }
            }


            JSONArray data2 = JsonUtil.getValueOfJsonArray(jsonObject, "saleList");//特价菜
            if (data2 != null && data2.length() > 0) {
                for (int i = 0; i < data2.length(); i++) {
                    JSONObject object2 = data2.getJSONObject(i);
//                    "id":2,
//                            "name":"青椒",
//                            "oprice":9,
//                            "cprice":4.5,
//                            "uprice":3,
//                            "specification":"3个/袋",
//                            "imagepath":"/imgs/produce/2.png",
//                            "type":"4",
//                            "produceTypeName":"蔬菜",
//                            "praisecount":3,
//                            "remark":null,
                    Dish dishItem = new Dish();
                    dishItem.dishId = JsonUtil.getValueOfInt(object2, "id");
                    dishItem.dishName = JsonUtil.getValueOfStr(object2, "name");
                    dishItem.dishOprice = JsonUtil.getValueOfDouble(object2, "oPrice");
                    dishItem.dishCprice = JsonUtil.getValueOfDouble(object2, "cPrice");
                    dishItem.dishUprice = JsonUtil.getValueOfDouble(object2, "uPrice");
                    dishItem.dishSpecification = JsonUtil.getValueOfStr(object2, "specification");
                    dishItem.dishPic = JsonUtil.getValueOfStr(object2, "imagePath");
                    dishItem.dishType = JsonUtil.getValueOfInt(object2, "type");
                    dishItem.dishProduceTypeName = JsonUtil.getValueOfStr(object2, "produceTypeName");
                    dishItem.dishPraisecount = JsonUtil.getValueOfInt(object2, "praiseCount");
                    dishItem.dishRemark = JsonUtil.getValueOfStr(object2, "remark");
                    dishItem.dishStock = JsonUtil.getValueOfInt(object2, "stock");
                    dishItem.dishTotalSold = JsonUtil.getValueOfInt(object2, "totalSold");
                    dishItem.qrCodePath = JsonUtil.getValueOfStr(object2, "qrCodePath");

                    feed.dishList.dishSpcialItems.add(dishItem);
                }
            }


            JSONArray data3 = JsonUtil.getValueOfJsonArray(jsonObject, "produceTypeList");//菜品类型


            DishType dishT = new DishType();
            dishT.dishTypeId = 0;//JsonUtil.getValueOfInt(object3, "id");
            dishT.dishTypeName = "全部";//JsonUtil.getValueOfStr(object3, "name");
            dishT.dishTypeState = 0;//JsonUtil.getValueOfInt(object3, "state");
            dishT.dishTypeSort = 0;//JsonUtil.getValueOfInt(object3, "sort");
            dishT.dishTypeRemark = "";//JsonUtil.getValueOfStr(object3, "remark");
            feed.dishList.dishTypes.add(dishT);

            if (data2 != null && data3.length() > 0) {
                for (int i = 0; i < data3.length(); i++) {
                    JSONObject object3 = data3.getJSONObject(i);
/// "id":1,
//                            "name":"主食",
//                            "state":1,
//                            "sort":1,
//                            "remark":""
                    DishType dishType = new DishType();
                    dishType.dishTypeId = JsonUtil.getValueOfInt(object3, "id");
                    dishType.dishTypeName = JsonUtil.getValueOfStr(object3, "name");
                    dishType.dishTypeState = JsonUtil.getValueOfInt(object3, "state");
                    dishType.dishTypeSort = JsonUtil.getValueOfInt(object3, "sort");
                    dishType.dishTypeRemark = JsonUtil.getValueOfStr(object3, "remark");

                    feed.dishList.dishTypes.add(dishType);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return feed;
    }


}
