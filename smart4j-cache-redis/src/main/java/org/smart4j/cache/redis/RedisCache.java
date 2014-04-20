package org.smart4j.cache.redis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.smart4j.cache.ISmartCache;
import org.smart4j.cache.SmartCacheException;
import redis.clients.jedis.Jedis;

public class RedisCache<K, V> implements ISmartCache<K, V> {

    private Jedis jedis;

    public RedisCache(Jedis jedis) {
        if (jedis == null) {
            throw new IllegalArgumentException("参数 jedis 非法！");
        }
        this.jedis = jedis;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(K key) throws SmartCacheException {
        try {
            if (key == null) {
                return null;
            } else {
                String str = jedis.get(key.toString());
                if (str == null) {
                    return null;
                } else {
                    return (V) str;
                }
            }
        } catch (Throwable t) {
            throw new SmartCacheException(t);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public V put(K key, V value) throws SmartCacheException {
        try {
            return (V) jedis.set(key.toString(), value.toString());
        } catch (Throwable t) {
            throw new SmartCacheException(t);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public V remove(K key) throws SmartCacheException {
        try {
            return (V) jedis.del(key.toString());
        } catch (Throwable t) {
            throw new SmartCacheException(t);
        }
    }

    @Override
    public void clear() throws SmartCacheException {
        try {
            jedis.flushAll();
        } catch (Throwable t) {
            throw new SmartCacheException(t);
        }
    }

    @Override
    public long size() {
        try {
            return jedis.dbSize();
        } catch (Throwable t) {
            throw new SmartCacheException(t);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<K> keys() {
        try {
            Set<K> keys = (Set<K>) jedis.keys("*");
            if (CollectionUtils.isNotEmpty(keys)) {
                return Collections.unmodifiableSet(new LinkedHashSet<K>(keys));
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
                for (K key : keys) {
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
