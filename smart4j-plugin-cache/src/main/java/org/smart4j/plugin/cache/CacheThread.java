package org.smart4j.plugin.cache;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.framework.util.MapUtil;

public class CacheThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(CacheThread.class);

    private static final long sleep_ms = 5 * 1000; // 5 秒钟

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        try {
            while (true) {
                // 遍历所有的 Cache Manager
                Iterable<CacheManager> cacheManagers = CacheFactory.getCacheManagers();
                for (CacheManager cacheManager : cacheManagers) {
                    // 遍历所有的 Cache
                    Iterable<Cache> caches = cacheManager.getCaches();
                    for (Cache cache : caches) {
                        // 遍历所有的 Duration Map
                        Map<Object, Duration> durationMap = cache.getDurations();
                        if (MapUtil.isNotEmpty(durationMap)) {
                            for (Object entrySet : durationMap.entrySet()) {
                                // 获取 Duration Map 中的 key 与 value
                                Map.Entry<Object, Duration> entry = (Map.Entry<Object, Duration>) entrySet;
                                Object cacheKey = entry.getKey();
                                Duration duration = entry.getValue();
                                // 获取 Duration 中的相关数据
                                long start = duration.getStart();   // 开始时间
                                long expiry = duration.getExpiry(); // 过期时间
                                // 获取当前时间
                                long current = System.currentTimeMillis();
                                // 判断是否已过期
                                if (current - start >= expiry) {
                                    // 若已过期，则首先移除 Cache（也会同时移除 Duration Map 中对应的条目）
                                    cache.remove(cacheKey);
                                }
                            }
                        }
                    }
                }
                // 使线程休眠
                sleep(sleep_ms);
            }
        } catch (InterruptedException e) {
            logger.error("运行 CacheThread 出错", e);
        }
    }
}
