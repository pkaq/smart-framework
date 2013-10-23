package com.smart.framework.cache;

import com.smart.framework.cache.impl.DefaultCache;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {

    private static final Map<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>();

    private static CacheManager instance = new CacheManager();

    public static CacheManager getInstance() {
        return instance;
    }

    private CacheManager() {
    }

    public Cache createCache(String cacheName) {
        Cache cache;
        if (!cacheMap.containsKey(cacheName)) {
            cache = new DefaultCache();
            cacheMap.put(cacheName, cache);
        } else {
            cache = cacheMap.get(cacheName);
        }
        return cache;
    }

    public void addCache(String cacheName, Cache cache) {
        if (!cacheMap.containsKey(cacheName)) {
            cacheMap.put(cacheName, cache);
        }
    }

    public void replaceCache(String cacheName, Cache cache) {
        cacheMap.put(cacheName, cache);
    }

    public List<Cache> getCacheAll() {
        List<Cache> cacheList = new ArrayList<Cache>();
        cacheList.addAll(CacheManager.cacheMap.values());
        return cacheList;
    }

    public void removeCache(String cacheName) {
        if (!cacheMap.containsKey(cacheName)) {
            cacheMap.remove(cacheName);
        }
    }

    public void removeCacheAll() {
        cacheMap.clear();
    }

    public int getCacheSize() {
        return cacheMap.size();
    }
}
