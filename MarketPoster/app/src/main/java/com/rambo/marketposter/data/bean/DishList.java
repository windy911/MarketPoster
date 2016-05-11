package com.rambo.marketposter.data.bean;

import java.util.ArrayList;

/**
 * Created by windy on 16/3/30.
 */
public class DishList {
    public ArrayList<Dish> dishNormalItems = new ArrayList<>();  //普通菜品
    public ArrayList<Dish> dishSpcialItems = new ArrayList<>();  //特价菜品
    public ArrayList<DishType> dishTypes = new ArrayList<>();    //菜品种类
}
