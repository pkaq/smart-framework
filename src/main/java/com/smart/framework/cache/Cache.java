package com.smart.framework.cache;

import java.util.List;

public interface Cache {

    void put(String key, Object value);

    <T> void putAll(String key, List<T> list);

    <T> T get(String key);

    <T> List<T> getAll();

    void remove(String key);

    void removeAll();

    int getSize();
}
