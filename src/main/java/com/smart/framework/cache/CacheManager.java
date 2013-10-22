package com.smart.framework.cache;

import java.util.List;

public interface CacheManager {

    Cache createCache(String cacheName);

    void addCache(String cacheName, Cache cache);

    void replaceCache(String cacheName, Cache cache);

    List<Cache> getCacheAll();

    void removeCache(String cacheName);

    void removeCacheAll();
}
