package org.smart4j.plugin.cache.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.smart4j.plugin.cache.Cache;
import org.smart4j.plugin.cache.Duration;

public class DefaultCache<K, V> implements Cache<K, V> {

    // 定义一个 Data Map，用于存放该 Cache 中所有的数据
    private final Map<K, V> dataMap = new ConcurrentHashMap<K, V>();

    // 定义一个 Duration Map，用于存放 Cache 的持续时间（开始时间 与 过期时长）
    private final Map<K, Duration> durationMap = new ConcurrentHashMap<K, Duration>();

    @Override
    public V get(K key) {
        if (key == null) {
            throw new NullPointerException("错误：参数 key 不能为空！");
        }
        return dataMap.get(key);
    }

    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new NullPointerException("错误：参数 key 不能为空！");
        }
        if (value == null) {
            throw new NullPointerException("错误：参数 value 不能为空！");
        }
        dataMap.put(key, value);
    }

    @Override
    public void put(K key, V value, long expiry) {
        put(key, value);
        if (expiry > 0) {
            durationMap.put(key, new Duration(System.currentTimeMillis(), expiry));
        }
    }

    @Override
    public void remove(K key) {
        if (key == null) {
            throw new NullPointerException("错误：参数 key 不能为空！");
        }
        dataMap.remove(key);
        durationMap.remove(key);
    }

    @Override
    public void clear() {
        dataMap.clear();
        durationMap.clear();
    }

    @Override
    public Map<K, Duration> getDurations() {
        return durationMap;
    }
}
