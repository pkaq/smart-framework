package com.smart.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.collections.map.ReferenceMap;

public class DefaultCacheManager implements ISmartCacheManager {

    private final ConcurrentMap<String, ISmartCache> cacheMap;

    public DefaultCacheManager() {
        this.cacheMap = new ConcurrentHashMap<String, ISmartCache>();
    }

    @SuppressWarnings("unchecked")
    public <K, V> ISmartCache<K, V> getCache(String name) throws SmartCacheException {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("参数 name 非法！");
        }
        try {
            // 根据 name 从 Cache Map 中获取 Cache，若为空，则创建 Cache，并将其放入 Cache Map 中
            ISmartCache<K, V> cache = cacheMap.get(name);
            if (cache == null) {
                // 创建一个基于 Map 的 Cache
                Map<K, V> map = new ReferenceMap(); // 强引用指向 key，弱引用指向 value
                cache = new DefalutCache<K, V>(map);
                cacheMap.putIfAbsent(name, cache);
            }
            return cache;
        } catch (Throwable t) {
            throw new SmartCacheException(t);
        }
    }
}
