package smart.cache.ehcache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import org.apache.commons.lang.StringUtils;
import smart.cache.ISmartCache;
import smart.cache.ISmartCacheManager;
import smart.cache.SmartCacheException;

public class EhcacheCacheManager implements ISmartCacheManager {

    private final CacheManager cacheManager;

    public EhcacheCacheManager() {
        cacheManager = CacheManager.newInstance();
    }

    @Override
    public final <K, V> ISmartCache<K, V> getCache(String name) throws SmartCacheException {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("参数 name 非法！");
        }
        try {
            Ehcache cache = cacheManager.getEhcache(name);
            if (cache == null) {
                cacheManager.addCache(name);
                cache = cacheManager.getCache(name);
            }
            return new EhcacheCache<K, V>(cache);
        } catch (Throwable t) {
            throw new SmartCacheException(t);
        }
    }
}