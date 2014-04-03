package com.smart.cache;

public interface CacheManager {

    /**
     * 根据名称获取 Cache 对象
     *
     * @param name Cache 名称
     * @return Cache 对象
     * @throws CacheException
     */
    <K, V> Cache<K, V> getCache(String name) throws CacheException;
}