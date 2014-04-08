package com.smart.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class DefalutCache<K, V> implements ISmartCache<K, V> {

    private final Map<K, V> map;

    public DefalutCache(Map<K, V> map) {
        if (map == null) {
            throw new IllegalArgumentException("参数 map 非法！");
        }
        this.map = map;
    }

    @Override
    public V get(K key) throws SmartCacheException {
        return map.get(key);
    }

    @Override
    public V put(K key, V value) throws SmartCacheException {
        return map.put(key, value);
    }

    @Override
    public V remove(K key) throws SmartCacheException {
        return map.remove(key);
    }

    @Override
    public void clear() throws SmartCacheException {
        map.clear();
    }

    @Override
    public long size() {
        return map.size();
    }

    @Override
    public Set<K> keys() {
        Set<K> keys = map.keySet();
        if (!keys.isEmpty()) {
            return Collections.unmodifiableSet(keys);
        }
        return Collections.emptySet();
    }

    @Override
    public Collection<V> values() {
        Collection<V> values = map.values();
        if (!values.isEmpty()) {
            return Collections.unmodifiableCollection(values);
        }
        return Collections.emptySet();
    }
}
