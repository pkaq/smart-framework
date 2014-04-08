package com.smart.cache.redis;

import com.smart.cache.ISmartCache;
import com.smart.cache.ISmartCacheManager;
import com.smart.cache.SmartCacheException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Administrator on 14-4-6.
 */
public class RedisManager implements ISmartCacheManager{

    private final ConcurrentMap<String, Redis> cacheMap;

    public RedisManager() {
        this.cacheMap = new ConcurrentHashMap<String, Redis>();
    }



    @Override
    public <K, V> ISmartCache<K, V> getCache(String name) throws SmartCacheException {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("参数 name 非法！");
        }
        try {
            // 根据 name 从 Cache Map 中获取 Cache，若为空，则创建 Cache，并将其放入 Cache Map 中
            Redis cache = cacheMap.get(name);
            if (cache == null) {
                JedisPool pool = new JedisPool(new JedisPoolConfig(),RedisProps.getIpByName(name));
                Jedis jedis= pool.getResource();
                cache= new Redis<K, V>(jedis);
            }
            return cache;
        } catch (Throwable t) {
            throw new SmartCacheException(t);
        }
    }
}
