package com.smart.framework.cache.impl;

import com.smart.framework.cache.Cache;
import com.smart.framework.util.CastUtil;
import com.smart.framework.util.ObjectUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class DefaultCache implements Cache {

    private static final Map<String, Object> dataMap = new ConcurrentSkipListMap<String, Object>();

    @Override
    public void put(String key, Object value) {
        dataMap.put(key, value);
    }

    @Override
    public <T> void putAll(String key, List<T> list) {
        for (T data : list) {
            long id = CastUtil.castLong(ObjectUtil.getFieldValue(data, "id"));
            dataMap.put(key + "-" + id, data);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object get(String key) {
        return dataMap.get(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object> getAll() {
        List<Object> valueList = new ArrayList<Object>();
        valueList.addAll(dataMap.values());
        return valueList;
    }

    @Override
    public void remove(String key) {
        if (dataMap.containsKey(key)) {
            dataMap.remove(key);
        }
    }

    @Override
    public void removeAll() {
        dataMap.clear();
    }

    @Override
    public int getSize() {
        return dataMap.size();
    }
}
