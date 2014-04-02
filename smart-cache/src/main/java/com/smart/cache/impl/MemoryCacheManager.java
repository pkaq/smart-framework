package com.smart.cache.impl;

import com.smart.cache.Cache;
import com.smart.cache.CacheException;
import java.util.Map;
import org.apache.commons.collections.map.ReferenceMap;

public class MemoryCacheManager extends AbstractCacheManager {

    @Override
    @SuppressWarnings("unchecked")
    protected <K, V> Cache<K, V> createCache(String name) throws CacheException {
        Map<K, V> map = new ReferenceMap(); // 强引用指向 key，弱引用指向 value
        return new MapCache<K, V>(name, map);
    }
}
