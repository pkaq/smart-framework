package org.smart4j.plugin.cache;

public interface CacheManager {

    // 获取所有的 Cache
    Iterable<Cache> getCaches();

    // 创建 Cache
    <K, V> Cache<K, V> createCache(String cacheName);

    // 获取 Cache
    <K, V> Cache<K, V> getCache(String cacheName);

    // 销毁指定 Cache
    void destroyCache(String cacheName);
}
