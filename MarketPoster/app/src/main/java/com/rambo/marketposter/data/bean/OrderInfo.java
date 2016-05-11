package com.rambo.marketposter.data.bean;

import java.io.Serializable;

/**
 * Created by windy on 16/4/14.
 */
public class OrderInfo implements Serializable{
//    "id":"1",
//            "orderNum":"2016CC1",
//            "OrderAmount":50,
//            "expressFee":0,
//            "totalAmount":50,
//            "contact":"张三",
//            "phone":"15050867126",
//            "payType":"支付宝",
//            "state":1,
//            "remark":"",
//            "createTime":"2016-04-06 15:30:25",

    public int orderId;
    public String orderNum;
    public double orderAmount;
    public double orderExpressFee;
    public double orderTotalAmount;
    public String orderContact;
    public String orderPhone;
    public String orderPayType;
    public int orderState;
    public String orderRemark;
    public String orderCreateTime;
}
