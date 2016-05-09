package com.sogokids.payment;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.HashMap;
import java.util.Map;

public class MapWrapper {
    private Map<String, String> params = new HashMap<String, String>();

    public void add(String key, String value) {
        params.put(key, value);
    }

    public String get(String key) {
        return params.get(key);
    }

    public void addAll(Map<String, String> params) {
        this.params = params;
    }

    @JSONField(serialize = false)
    public Map<String, String> getAll() {
        return params;
    }
}
