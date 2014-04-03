package com.smart.cache.test;

import com.smart.cache.ISmartCache;
import com.smart.cache.ISmartCacheManager;
import com.smart.cache.impl.DefaultCacheManager;
import org.junit.Test;

public class CacheTest {

    @Test
    public void test() {
        ISmartCacheManager cacheManager = new DefaultCacheManager();
        ISmartCache<String, Object> cache = cacheManager.getCache("cache_name");

        for (int i = 1; i <= 100; i++) {
            cache.put("key" + i, "value" + i);
        }

        for (int i = 1; i <= 100; i++) {
            Object value = cache.get("key" + i);
            System.out.println("key" + i + " => " + value);
        }
    }
}
