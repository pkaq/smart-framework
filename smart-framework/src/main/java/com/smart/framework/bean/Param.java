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

    @SuppressWarnings("unchecked")
    public <T> T get(String name, Class<T> type) {
        Object value = fieldMap.get(name);
        if (type.equals(int.class) || type.equals(Integer.class)) {
            value = CastUtil.castInt(value);
        } else if (type.equals(long.class) || type.equals(Long.class)) {
            value = CastUtil.castLong(value);
        } else if (type.equals(double.class) || type.equals(Double.class)) {
            value = CastUtil.castDouble(value);
        } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            value = CastUtil.castBoolean(value);
        } else {
            value = CastUtil.castString(value);
        }
        return (T) value;
    }
}
