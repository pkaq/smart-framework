package org.smart4j.cache;

import java.util.Collection;
import java.util.Set;

public interface SmartCache<K, V> {

    /**
     * 根据 Key 从 Cache 中获取 Value
     *
     * @param key Key
     * @return 已获取的 Value
     * @throws SmartCacheException
     */
    V get(K key) throws SmartCacheException;

    /**
     * 将 Key 与 Value 放入 Cache 中
     *
     * @param key Key
     * @param value Value
     * @return 放入前的 Value
     * @throws SmartCacheException
     */
    V put(K key, V value) throws SmartCacheException;

    /**
     * 根据 Key 从 Cache 中移除 Value
     *
     * @param key Key
     * @return 移除前的 Value
     * @throws SmartCacheException
     */
    V remove(K key) throws SmartCacheException;

    /**
     * 清空 Cache
     *
     * @throws SmartCacheException
     */
    void clear() throws SmartCacheException;

    /**
     * 获取 Cache 的 Key-Value 个数
     *
     * @return Key-Value 个数
     */
    long size();

    /**
     * 获取 Cache 中所有的 Key 集合
     *
     * @return Key 集合
     */
    Set<K> keys();

    /**
     * 获取 Cache 中所有的 Value 集合
     *
     * @return Value 集合
     */
    Collection<V> values();
}