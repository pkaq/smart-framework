package com.smart.framework.cache.impl;

import com.smart.framework.cache.Cache;
import com.smart.framework.cache.CacheManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultCacheManager implements CacheManager {

    private static final Map<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>();

    private static DefaultCacheManager instance = new DefaultCacheManager();

    public static DefaultCacheManager getInstance() {
        return instance;
    }

    private DefaultCacheManager() {
    }

    @Override
    public Cache createCache(String cacheName) {
        Cache cache;
        if (!cacheMap.containsKey(cacheName)) {
            cache = new DefaultCache();
            replaceCache(cacheName, cache);
        } else {
            cache = cacheMap.get(cacheName);
        }
        return cache;
    }

    @Override
    public void addCache(String cacheName, Cache cache) {
        if (!cacheMap.containsKey(cacheName)) {
            replaceCache(cacheName, cache);
        }
    }

    @Override
    public void replaceCache(String cacheName, Cache cache) {
        cacheMap.put(cacheName, cache);
    }

    @Override
    public List<Cache> getCacheAll() {
        List<Cache> cacheList = new ArrayList<Cache>();
        cacheList.addAll(DefaultCacheManager.cacheMap.values());
        return cacheList;
    }

    @Override
    public void removeCache(String cacheName) {
        checkCacheName(cacheName);
        cacheMap.remove(cacheName);
    }

    @Override
    public void removeCacheAll() {
        cacheMap.clear();
    }

    private void checkCacheName(String cacheName) {
        if (!cacheMap.containsKey(cacheName)) {
            throw new RuntimeException("无法指定的获取缓存！" + cacheName);
        }
    }
}
