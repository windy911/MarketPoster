package com.rambo.marketposter.utils;


import com.rambo.marketposter.data.bean.Dish;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonUtil {

    JSONObject mJSONObject;

    public JsonUtil(String jsonStr) {
        try {
            mJSONObject = new JSONObject(jsonStr);
        } catch (Exception e) {
//			e.printStackTrace();
            mJSONObject = new JSONObject();
        }
    }

    public String getString(String key) {
        if (null != mJSONObject) {
            getValueOfStr(mJSONObject, key);
        }
        return null;
    }

    public void put(String key, String newValue, String oldValue) {
        if (null != mJSONObject) {
            if (newValue == null) {
                return;
            }
            if (oldValue == null || !oldValue.equals(newValue)) {
                try {
                    mJSONObject.put(key, newValue);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String toString() {
        if (null == mJSONObject) {
            return null;
        }
        return mJSONObject.toString();
    }

    public static String getValueOfStr(JSONObject jsonOb, String key) {
        try {
            return jsonOb.has(key) ? jsonOb.getString(key) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static Double getValueOfDouble(JSONObject jsonOb, String key) {
        try {
            return jsonOb.has(key) ? jsonOb.getDouble(key) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getValueOfStr2(JSONObject jsonOb, String key) {
        try {
            return jsonOb.has(key) ? jsonOb.getString(key) : "";
        } catch (Exception e) {
            return "";
        }
    }

    public static int getValueOfInt(JSONObject jsonOb, String key) {
        try {
            return jsonOb.has(key) ? jsonOb.getInt(key) : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getValueOfStrInt(JSONObject jsonOb, String key) {
        try {
            String tmp = jsonOb.has(key) ? jsonOb.getString(key) : null;
            if (StringUtil.isNotEmpty(tmp)) {
                return Integer.parseInt(tmp);
            }
        } catch (Exception e) {
        }
        return 0;
    }

    public static boolean getValueOfboolean(JSONObject jsonOb, String key) {
        try {
            return jsonOb.has(key) ? jsonOb.getBoolean(key) : false;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return false;

    }

    public static JSONObject getValueOfObject(JSONObject jsonOb, String key) {
        try {
            return jsonOb.has(key) ? jsonOb.getJSONObject(key) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static JSONArray getValueOfJsonArray(JSONObject jsonOb, String key) {
        try {
            return jsonOb.has(key) ? jsonOb.getJSONArray(key) : null;
        } catch (Exception e) {
            return null;
        }
    }


    public static String dishListToJsonString(ArrayList<Dish> dishArrayList) {
        String jsonresult = "";//定义返回字符串
        JSONObject object = new JSONObject();//创建一个总的对象，这个对象对整个json串
        try {
            JSONArray jsonarray = new JSONArray();//json数组，里面包含的内容为pet的所有对象


            for (int i = 0; i < dishArrayList.size(); i++) {
                JSONObject jsonObj = new JSONObject();//pet对象，json形式
                Dish dish = dishArrayList.get(i);
                jsonObj.put("produceId", dish.dishId);//向pet对象里面添加值
                jsonObj.put("count", dish.dishCount);
                jsonarray.put(jsonObj);//向json数组里面添加pet对象
            }
//            object.put("data", jsonarray);//向总对象里面添加包含pet的数组
            jsonresult = jsonarray.toString();//生成返回字符串
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Mylog.d("dishListToJsonString : ", jsonresult);
        return jsonresult;
    }
}
