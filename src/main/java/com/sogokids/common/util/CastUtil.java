package com.sogokids.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class CastUtil {
    public static <T> T toObject(JSON json, Class<T> clazz) {
        return JSON.toJavaObject(json, clazz);
    }

    public static <T> List<T> toList(JSON json, Class<T> clazz) {
        JSONArray jsonArray = (JSONArray) json;

        List<T> list = new ArrayList<T>();
        for (int i = 0; i < jsonArray.size(); i++) {
            list.add(jsonArray.getObject(i, clazz));
        }

        return list;
    }
}
