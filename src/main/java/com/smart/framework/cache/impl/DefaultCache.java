package com.smart.framework.cache.impl;

import com.smart.framework.cache.Cache;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultCache implements Cache {

    private static final Map<String, Object> dataMap = new ConcurrentHashMap<String, Object>();

    @Override
    public void put(String key, Object value) {
        dataMap.put(key, value);
    }

    @Override
    public Object get(String key) {
        checkKey(key);
        return dataMap.get(key);
    }

    @Override
    public List<Object> getAll() {
        List<Object> valueList = new ArrayList<Object>();
        valueList.addAll(dataMap.values());
        return valueList;
    }

    @Override
    public void remove(String key) {
        checkKey(key);
        dataMap.remove(key);
    }

    @Override
    public void removeAll() {
        dataMap.clear();
    }

    private void checkKey(String key) {
        if (!dataMap.containsKey(key)) {
            throw new RuntimeException("无法指定的获取数据！" + key);
        }
    }
}
