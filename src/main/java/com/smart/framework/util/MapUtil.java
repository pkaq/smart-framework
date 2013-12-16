package com.smart.framework.util;

import java.util.Map;
import org.apache.commons.collections.MapUtils;

public class MapUtil {

    // 判断 Map 是否非空
    public static boolean isNotEmpty(Map<?, ?> map) {
        return MapUtils.isNotEmpty(map);
    }

    // 判断 Map 是否为空
    public static boolean isEmpty(Map<?, ?> map) {
        return MapUtils.isEmpty(map);
    }

    // 转置 Map
    @SuppressWarnings("unchecked")
    public static <K, V> Map<V, K> inverse(Map<K, V> map) {
        return MapUtils.invertMap(map);
    }
}
