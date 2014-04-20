package org.smart4j.plugin.cache;

import java.util.Map;

public interface Cache<K, V> {

    // 从 Cache 中获取数据
    V get(K key);

    // 将数据放入 Cache 中
    void put(K key, V value);

    // 将数据放入 Cache 中，指定有效期（ms）
    void put(K key, V value, long expiry);

    // 从 Cache 中移除数据
    void remove(K key);

    // 清空 Cache
    void clear();

    // 获取所有的 Duration
    Map<K, Duration> getDurations();
}
