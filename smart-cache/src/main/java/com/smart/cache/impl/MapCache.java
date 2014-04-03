package com.smart.cache.impl;

import com.smart.cache.Cache;
import com.smart.cache.CacheException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class MapCache<K, V> implements Cache<K, V> {

    private final String name;
    private final Map<K, V> map;

    public MapCache(String name, Map<K, V> map) {
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
    public V get(K key) throws CacheException {
        return map.get(key);
    }

    @Override
    public V put(K key, V value) throws CacheException {
        return map.put(key, value);
    }

    @Override
    public V remove(K key) throws CacheException {
        return map.remove(key);
    }

    @Override
    public void clear() throws CacheException {
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
