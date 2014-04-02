package com.smart.cache;

public interface CacheManager {

    <K, V> Cache<K, V> getCache(String name) throws CacheException;
}