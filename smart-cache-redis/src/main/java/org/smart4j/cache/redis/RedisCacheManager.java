package org.smart4j.cache.redis;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.lang.StringUtils;
import org.smart4j.cache.SmartCache;
import org.smart4j.cache.SmartCacheException;
import org.smart4j.cache.SmartCacheManager;
import redis.clients.jedis.Jedis;

public class RedisCacheManager implements SmartCacheManager {

    private final ConcurrentMap<String, SmartCache> cacheMap;

    public RedisCacheManager() {
        this.cacheMap = new ConcurrentHashMap<String, SmartCache>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> SmartCache<K, V> getCache(String name) throws SmartCacheException {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("参数 name 非法！");
        }
        try {
            SmartCache<K, V> cache = cacheMap.get(name);
            if (cache == null) {
                String host = SmartProps.getHost();
                int port = SmartProps.getPort();
                Jedis jedis = new Jedis(host, port);
                cache = new RedisCache<K, V>(jedis);
                cacheMap.putIfAbsent(name, cache);
            }
            return cache;
        } catch (Throwable t) {
            throw new SmartCacheException(t);
        }
    }
}
