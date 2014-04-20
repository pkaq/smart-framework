package org.smart4j.plugin.cache.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.smart4j.plugin.cache.Cache;
import org.smart4j.plugin.cache.CacheException;
import org.smart4j.plugin.cache.CacheManager;

public class DefaultCacheManager implements CacheManager {

    // 定义一个 Cache Map，用于存放该 Cache Manager 中所有的 Cache
    private final Map<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>();

    @Override
    public Iterable<Cache> getCaches() {
        return cacheMap.values();
    }

    @Override
    public <K, V> Cache<K, V> createCache(String cacheName) {
        if (cacheName == null) {
            throw new NullPointerException("错误：参数 cacheName 不能为空！");
        }
        if (cacheMap.containsKey(cacheName)) {
            throw new CacheException("错误：同名的 Cache 已存在，无法创建！cacheName: " + cacheName);
        }
        Cache<K, V> cache = new DefaultCache<K, V>();
        cacheMap.put(cacheName, cache);
        return cache;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Cache<K, V> getCache(String cacheName) {
        if (cacheName == null) {
            throw new NullPointerException("错误：参数 cacheName 不能为空！");
        }
        return cacheMap.get(cacheName);
    }

    @Override
    public void destroyCache(String cacheName) {
        if (cacheName == null) {
            throw new NullPointerException("错误：参数 cacheName 不能为空！");
        }
        Cache cache = getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }
}
