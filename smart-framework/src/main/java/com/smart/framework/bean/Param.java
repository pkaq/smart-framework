package com.smart.framework.bean;

import com.smart.framework.base.BaseBean;
import com.smart.framework.util.CastUtil;
import java.util.Map;

public class Param extends BaseBean {

    private final Map<String, Object> fieldMap;

    public Param(Map<String, Object> fieldMap) {
        this.fieldMap = fieldMap;
    }

    public Map<String, Object> getFieldMap() {
        return fieldMap;
    }

    public int getInt(String name) {
        return CastUtil.castInt(get(name));
    }

    public long getLong(String name) {
        return CastUtil.castLong(get(name));
    }

    public double getDouble(String name) {
        return CastUtil.castDouble(get(name));
    }

    public String getString(String name) {
        return CastUtil.castString(get(name));
    }

    private Object get(String name) {
        return fieldMap.get(name);
    }
}
