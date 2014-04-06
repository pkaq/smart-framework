package com.smart.cache.redis;

import com.smart.cache.ISmartCache;
import com.smart.cache.ISmartCacheManager;
import com.smart.cache.SmartCacheException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by Administrator on 14-4-6.
 */
public class RedisManager implements ISmartCacheManager,CacheConstant {
    private final JedisPool pool;

    public RedisManager() {
        pool = new JedisPool(new JedisPoolConfig(),CACHE_IP);
    }

    @Override
    public <K, V> ISmartCache<K, V> getCache(String name) throws SmartCacheException {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("参数 name 非法！");
        }
        try {
            Jedis jedis = pool.getResource();
            if (jedis == null) {
                return null;
            }
            return new Redis<K, V>(jedis);
        } catch (Throwable t) {
            throw new SmartCacheException(t);
        }
    }
}
