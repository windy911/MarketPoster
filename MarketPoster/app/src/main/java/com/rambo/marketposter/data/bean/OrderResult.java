package com.rambo.marketposter.data.bean;

import java.io.Serializable;

/**
 * Created by windy on 16/4/14.
 */
public class OrderResult implements Serializable{

    public int state;  //订单状态
    public String imgPath;//二维码支付图片

    public boolean isPayed(){
        return 1 == state;
    }

}
