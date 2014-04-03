package com.smart.cache.ehcache;

import com.smart.cache.ISmartCache;
import com.smart.cache.ISmartCacheManager;
import com.smart.cache.SmartCacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

public class EhCacheManager implements ISmartCacheManager {

    private final CacheManager cacheManager;

    public EhCacheManager() {
        cacheManager = CacheManager.newInstance();
    }

    public final <K, V> ISmartCache<K, V> getCache(String name) throws SmartCacheException {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("参数 name 非法！");
        }
        try {
            Ehcache cache = cacheManager.getEhcache(name);
            if (cache == null) {
                cacheManager.addCache(name);
                cache = cacheManager.getCache(name);
            }
            return new EhCache<K, V>(cache);
        } catch (Throwable t) {
            throw new SmartCacheException(t);
        }
    }
}