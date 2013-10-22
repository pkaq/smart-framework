package com.smart.framework.cache;

import java.util.List;

public interface Cache {

    void put(String key, Object value);

    Object get(String key);

    List<Object> getAll();

    void remove(String key);

    void removeAll();
}
