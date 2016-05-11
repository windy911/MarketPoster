package com.rambo.marketposter.data.bean;

/**
 * Created by windy on 16/4/7.i
 * 一种菜类型
 */
public class DishType {
//
//    "id":1,
//            "name":"主食",
//            "state":1,
//            "sort":1,
//            "remark":""


    public String dishTypeName;
    public int dishTypeId;
    public int dishTypeState;
    public int dishTypeSort;
    public String dishTypeRemark;


    public boolean isSelected;

    public DishType() {
    }

    public DishType(int dishTypeId, String dishTypeName) {
        this.dishTypeId = dishTypeId;
        this.dishTypeName = dishTypeName;
    }

    public void performSelected() {
        isSelected = !isSelected;
    }
}
