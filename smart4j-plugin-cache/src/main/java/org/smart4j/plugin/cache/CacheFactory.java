package org.smart4j.plugin.cache;

import java.util.HashMap;
import java.util.Map;
import org.smart4j.framework.util.StringUtil;
import org.smart4j.plugin.cache.impl.DefaultCacheManager;

public class CacheFactory {

    // 定义一个 CacheManager Map，用于存放目标类与 CacheManager 的对应关系（一个目标类对应一个 CacheManager），目标类一般为 Service 类
    private static final Map<Class<?>, CacheManager> cacheManagerMap = new HashMap<Class<?>, CacheManager>();

    public static Iterable<CacheManager> getCacheManagers() {
        return cacheManagerMap.values();
    }

    public static <K, V> Cache<K, V> createCache(Class<?> cls, String cacheName) {
        // 若 Cache 不存在，则创建一个新的 Cache，以便下次可直接获取
        Cache<K, V> cache = null;
        if (cls != null && StringUtil.isNotEmpty(cacheName)) {
            CacheManager cacheManager = getCacheManager(cls);
            if (cacheManager != null) {
                cache = cacheManager.getCache(cacheName);
                if (cache == null) {
                    cache = cacheManager.createCache(cacheName);
                }
            }
        }
        return cache;
    }

    public static CacheManager getCacheManager(Class<?> targetClass) {
        // 先从 CacheManager Map 中获取
        // 若不存在，则创建一个新的 CacheManager，并将其放入 CacheManager Map 中
        // 若存在，则直接返回
        if (!cacheManagerMap.containsKey(targetClass)) {
            CacheManager cacheManager = new DefaultCacheManager();
            cacheManagerMap.put(targetClass, cacheManager);
        }
        return cacheManagerMap.get(targetClass);
    }
}
