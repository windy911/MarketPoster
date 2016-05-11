package com.rambo.marketposter.data.bean;

import android.content.Context;

import com.rambo.marketposter.R;
import com.rambo.marketposter.utils.StringUtil;

/**
 * Created by windy on 16/4/7.i
 * 一种菜
 */
public class Dish {
//    "id":18,
//            "name":"玉米",
//            "oprice":5,
//            "cprice":2.5,
//            "uprice":2.5,
//            "specification":"2个/袋",
//            "imagepath":"http://localhost:8080/cczz/imgs/produce/18.png",
//            "type":"8",
//            "produceTypeName":"特价",
//            "praisecount":null,
//            "remark":null,
//    "stock":10,
//            "totalSold":1,


    public static int MAX_NUM = 99;
    public static int MIN_NUM = 0;

    public int dishId;
    public String dishName;
    public String dishPic;
    public int dishType;//菜的大类
    public double dishOprice;//原价
    public double dishCprice;//现价
    public double dishUprice;//单价
    public String dishSpecification;//规格
    public String dishProduceTypeName;//“特价”
    public int dishCount;
    public int dishStock;
    public int dishTotalSold;
    public String dishRemark;//备注
    public int dishPraisecount;//赞的数量
    public String qrCodePath;//二维码


    public boolean isSelected;//是否选中 删除
    public boolean isFav;//是否是喜欢


    public void setSelectedClicked() {
        isSelected = !isSelected;
    }

    public boolean addCount() {
        if (dishCount < MAX_NUM) {
            dishCount += 1;
        } else {
//            ToastUtil.show(MyApplication.getAppContext(), "超过最大限制");
            return false;
        }

        return true;
    }

    public boolean delCount() {
        if (dishCount > 0) {
            dishCount -= 1;
            return true;
        }
        return false;
    }

    public void performSelected() {
        isSelected = !isSelected;
    }

    public int getDishPariseCount() {
        return isFav ? (dishPraisecount + 1) : dishPraisecount;
    }

    public String getDanJia() {
        return "单价：" + String.valueOf(dishUprice);
    }

    public String getReMark() {
        return "描述：" + (StringUtil.isEmpty(dishRemark) ? "" : dishRemark);
    }

    public String getGuiGe() {
        return "规格：" + dishSpecification;
    }

    public String getJiaGe() {
        return "价格：" + String.valueOf(dishCprice) + " 元";
    }

    //    textView.setText(Html.fromHtml("<u>"+"0123456"+"</u>"));
    public String getJiaGeHtmlULine() {
        return "价格：" + String.valueOf(dishOprice) + " 元";
    }

    public String getJiaGe2() {
        return "现价：" + String.valueOf(dishCprice) + " 元";
    }


    //价格是 oprice equals cprice
    public boolean isJiaGeEqualOC() {
        return dishOprice == dishCprice;
    }

    //计算总价  和  销售状况
    public String getResult() {

        if (isSoldOver()) {
            return "已售罄";
        }

        double result = dishCount * dishCprice;
        if (dishCount != 0) {
            return "￥" + StringUtil.getDouble(result);
        }


        return "";
    }

    public int getResultColor(Context context) {
        if (isSoldOver()) return context.getResources().getColor(R.color.text_black_alpha30);
        else return context.getResources().getColor(R.color.tomato);
    }

    public Double getTotalPrice() {
        return dishCount * dishCprice;
    }

    public boolean isSoldOver() {
        return dishStock == 0;
    }


    //判断是否相等，是同一菜品
    public boolean isSameDishId(Dish dish) {
        if (dish == null) return false;
        return dish.dishId == this.dishId;
    }
}
