package com.smart.cache.impl;

import com.smart.cache.ISmartCache;
import com.smart.cache.SmartCacheException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class DefalutCache<K, V> implements ISmartCache<K, V> {

    private final String name;
    private final Map<K, V> map;

    public DefalutCache(String name, Map<K, V> map) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("参数 name 非法！");
        }
        if (map == null) {
            throw new IllegalArgumentException("参数 map 非法！");
        }
        this.name = name;
        this.map = map;
    }

    public String getName() {
        return name;
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
    public int size() {
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
