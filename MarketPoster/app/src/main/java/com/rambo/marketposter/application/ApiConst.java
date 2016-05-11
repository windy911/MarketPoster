package com.rambo.marketposter.application;

public class ApiConst {

//    public static final String API_SERVER_IP = "http://210.22.128.2:6066/";
//    public static final String API_SERVER_IP = "http://m.sumeijiaocheng.com:8084/";
    public static final String API_SERVER_IP = "http://m.sumeijiaocheng.com:8084/";

    //首页GRID数据
    public static final String API_MAIN_GRID_PAGE = "cczz/v1/home/index";

    //获取菜品列表
    public static final String API_GET_PRODUCE_LIST = "cczz/v1/produce/getProduceList";

    //点赞 喜欢
    public static final String API_PRODUCT_PRAISE = "cczz/v1/produce/praise";

    //生成订单
    public static final String API_GENERATEORDER = "cczz/v1/order/generateOrder";

    //保存订单
    public static final String API_SAVE_ORDER = "cczz/v1/order/saveOrder";

    //检查订单状态做跳转
    public static final String API_CHECK_ORDER = "cczz/v1/order/checkOrder";
}
