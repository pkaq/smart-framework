package org.smart4j.plugin.cache;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.smart4j.framework.aop.proxy.ProxyChain;
import org.smart4j.framework.core.ClassHelper;
import org.smart4j.framework.plugin.PluginProxy;
import org.smart4j.framework.util.ArrayUtil;
import org.smart4j.plugin.cache.annotation.Cachable;
import org.smart4j.plugin.cache.annotation.CacheClear;
import org.smart4j.plugin.cache.annotation.CachePut;

public class CacheProxy extends PluginProxy {

    // 创建一个重入锁
    private final Lock lock = new ReentrantLock();

    @Override
    public List<Class<?>> getTargetClassList() {
        // 设置目标类列表（获取带有 @Cachable 注解的目标类）
        return ClassHelper.getClassListByAnnotation(Cachable.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object doProxy(ProxyChain proxyChain) throws Throwable {
        lock.lock();
        try {
            // 定义方法返回值
            Object result = null;
            // 获取目标方法
            Class<?> cls = proxyChain.getTargetClass();
            Method method = proxyChain.getTargetMethod();
            Object[] params = proxyChain.getMethodParams();
            // 判断不同类型的 Cache 注解
            if (method.isAnnotationPresent(CachePut.class)) {
                // 从 @CachePut 注解中获取相关属性
                CachePut cachePut = method.getAnnotation(CachePut.class);
                String cacheName = cachePut.value();
                long expiry = method.getAnnotation(CachePut.class).expiry();
                // 若为 @CachePut 注解，则首先从 Cache 中获取
                // 若 Cache 中不存在，则从 DB 中获取，最后放入 Cache 中
                Cache cache = CacheFactory.createCache(cls, cacheName);
                if (cache != null) {
                    // Cache Key = 方法名-参数
                    String cacheKey = method.getName() + "-" + Arrays.toString(params);
                    result = cache.get(cacheKey); // 从 Cache 中获取
                    if (result == null) {
                        result = proxyChain.doProxyChain(); // 从 DB 中获取
                        if (result != null) {
                            cache.put(cacheKey, result, expiry); // 放入 Cache 中
                        }
                    }
                }
            } else if (method.isAnnotationPresent(CacheClear.class)) {
                // 若为 @CacheRemove 注解，则首先进行数据库操作，然后刷新缓存
                result = proxyChain.doProxyChain();
                // 从 @CacheClear 注解中获取相应的 Cache Name（一个或多个），并通过 CacheManager 销毁所对应的 Cache
                CacheClear cacheClear = method.getAnnotation(CacheClear.class);
                String[] cacheNames = cacheClear.value();
                if (ArrayUtil.isNotEmpty(cacheNames)) {
                    CacheManager cacheManager = CacheFactory.getCacheManager(cls);
                    for (String cacheName : cacheNames) {
                        cacheManager.destroyCache(cacheName);
                    }
                }
            } else {
                // 若不带有任何的 Cache 注解，则直接进行数据库操作
                result = proxyChain.doProxyChain();
            }
            // 返回结果
            return result;
        } finally {
            lock.unlock();
        }
    }
}
