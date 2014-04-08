package com.smart.cache.redis;

import com.smart.cache.ISmartCache;
import com.smart.cache.SmartCacheException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import redis.clients.jedis.Jedis;

/**
 * Created by Administrator on 14-4-7.
 */
public class Redis<K,V> implements ISmartCache<K,V> {

    private Jedis cache;

    public Redis(Jedis cache) {
        if (cache == null) {
            throw new IllegalArgumentException("参数 cache 非法！");
        }
        this.cache = cache;
    }

    @Override
    public V get(K key) throws SmartCacheException {
        try {
            if (key == null) {
                return null;
            } else {
                String str= cache.get(key.toString());
                if (str == null) {
                    return null;
                } else {
                    return (V)str;
                }
            }
        } catch (Throwable t) {
            throw new SmartCacheException(t);
        }
    }

    @Override
    public V put(K key, V value) throws SmartCacheException {
        try {
            return (V)cache.set(key.toString(),value.toString());
        } catch (Throwable t) {
            throw new SmartCacheException(t);
        }
    }

    @Override
    public V remove(K key) throws SmartCacheException {
        try {
            return (V)cache.del(key.toString());
        } catch (Throwable t) {
            throw new SmartCacheException(t);
        }
    }

    @Override
    public void clear() throws SmartCacheException {
        try {
            cache.flushAll();
        } catch (Throwable t) {
            throw new SmartCacheException(t);
        }
    }

    @Override
    public long size() {
        try {
            return cache.dbSize();
        } catch (Throwable t) {
            throw new SmartCacheException(t);
        }
    }

    @Override
    public Set<K> keys() {
        try {
            Set<K> keys =(Set<K>)cache.keys("*");
            if (CollectionUtils.isNotEmpty(keys)) {
                return keys;
            } else {
                return Collections.emptySet();
            }
        } catch (Throwable t) {
            throw new SmartCacheException(t);
        }

    }

    @Override
    public Collection<V> values() {
        try {
            Set<K> keys = keys();
            if (CollectionUtils.isNotEmpty(keys)) {
                List<V> values = new ArrayList<V>(keys.size());
                for(K key:keys){
                    V value = get(key);
                    if (value != null) {
                        values.add(value);
                    }
                }
                return Collections.unmodifiableList(values);
            } else {
                return Collections.emptyList();
            }
        } catch (Throwable t) {
            throw new SmartCacheException(t);
        }
    }
}
