package com.smart.framework.cache;

import java.util.List;

public class Client {

    public static void main(String[] args) {
        CacheManager cacheManager = CacheManager.getInstance();

        Cache cache = cacheManager.createCache("sample_cache");

        cache.put("A", 1);
        cache.put("B", 2);

        Integer a = cache.get("A");
        System.out.println("a: " + a);

        List<Object> valueList1 = cache.getAll();
        System.out.println(valueList1.size());

        cache.remove("A");

        List<Object> valueList2 = cache.getAll();
        System.out.println(valueList2.size());

        Cache cache2 = cacheManager.createCache("sample_cache");

        List<Object> valueList3 = cache2.getAll();
        System.out.println(valueList3.size());
    }
}
