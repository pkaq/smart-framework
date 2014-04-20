package org.smart4j.cache.redis;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.lang.StringUtils;
import org.smart4j.cache.ISmartCache;
import org.smart4j.cache.ISmartCacheManager;
import org.smart4j.cache.SmartCacheException;
import redis.clients.jedis.Jedis;

public class RedisCacheManager implements ISmartCacheManager {

    private final ConcurrentMap<String, ISmartCache> cacheMap;

    public RedisCacheManager() {
        this.cacheMap = new ConcurrentHashMap<String, ISmartCache>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> ISmartCache<K, V> getCache(String name) throws SmartCacheException {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("参数 name 非法！");
        }
        try {
            ISmartCache<K, V> cache = cacheMap.get(name);
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
