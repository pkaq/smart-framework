package org.smart4j.cache;

public interface SmartCacheManager {

    /**
     * 根据名称获取 Cache 对象
     *
     * @param name Cache 名称
     * @return Cache 对象
     * @throws SmartCacheException
     */
    <K, V> SmartCache<K, V> getCache(String name) throws SmartCacheException;
}