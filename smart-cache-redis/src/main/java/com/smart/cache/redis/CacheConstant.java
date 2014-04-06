package com.smart.cache.redis;
import com.smart.cache.helper.ConfigHelper;

/**
 * Created by Administrator on 14-3-17.
 */
public interface CacheConstant {
    String CACHE_IP = ConfigHelper.getConfigString("cache.ip");
    String CACHE_TYPE =ConfigHelper.getConfigString("cache.type");
}
