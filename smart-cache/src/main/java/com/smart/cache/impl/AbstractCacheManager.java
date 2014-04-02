package com.smart.cache.impl;

import com.smart.cache.Cache;
import com.smart.cache.CacheException;
import com.smart.cache.CacheManager;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractCacheManager implements CacheManager {

    private final ConcurrentMap<String, Cache> cacheMap;

    public AbstractCacheManager() {
        this.cacheMap = new ConcurrentHashMap<String, Cache>();
    }

    @SuppressWarnings("unchecked")
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("参数 name 非法！");
        }
        // 根据 name 从 Cache Map 中获取 Cache，若为空，则创建 Cache，并将其放入 Cache Map 中
        Cache<K, V> cache = cacheMap.get(name);
        if (cache == null) {
            cache = createCache(name);
            cacheMap.putIfAbsent(name, cache);
        }
        return cache;
    }

    protected abstract <K, V> Cache<K, V> createCache(String name) throws CacheException;
}