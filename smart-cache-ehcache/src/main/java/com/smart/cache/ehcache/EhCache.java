package com.smart.cache.ehcache;

import com.smart.cache.ISmartCache;
import com.smart.cache.SmartCacheException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.commons.collections.CollectionUtils;

public class EhCache<K, V> implements ISmartCache<K, V> {

    private Ehcache cache;

    public EhCache(Ehcache cache) {
        if (cache == null) {
            throw new IllegalArgumentException("参数 cache 非法！");
        }
        this.cache = cache;
    }

    @SuppressWarnings("unchecked")
    public V get(K key) throws SmartCacheException {
        try {
            if (key == null) {
                return null;
            } else {
                Element element = cache.get(key);
                if (element == null) {
                    return null;
                } else {
                    return (V) element.getObjectValue();
                }
            }
        } catch (Throwable t) {
            throw new SmartCacheException(t);
        }
    }

    public V put(K key, V value) throws SmartCacheException {
        try {
            V previous = get(key);
            Element element = new Element(key, value);
            cache.put(element);
            return previous;
        } catch (Throwable t) {
            throw new SmartCacheException(t);
        }
    }

    public V remove(K key) throws SmartCacheException {
        try {
            V previous = get(key);
            cache.remove(key);
            return previous;
        } catch (Throwable t) {
            throw new SmartCacheException(t);
        }
    }

    public void clear() throws SmartCacheException {
        try {
            cache.removeAll();
        } catch (Throwable t) {
            throw new SmartCacheException(t);
        }
    }

    public long size() {
        try {
            return cache.getSize();
        } catch (Throwable t) {
            throw new SmartCacheException(t);
        }
    }

    @SuppressWarnings("unchecked")
    public Set<K> keys() {
        try {
            List<K> keys = cache.getKeys();
            if (CollectionUtils.isNotEmpty(keys)) {
                return Collections.unmodifiableSet(new LinkedHashSet<K>(keys));
            } else {
                return Collections.emptySet();
            }
        } catch (Throwable t) {
            throw new SmartCacheException(t);
        }
    }

    @SuppressWarnings("unchecked")
    public Collection<V> values() {
        try {
            List<K> keys = cache.getKeys();
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