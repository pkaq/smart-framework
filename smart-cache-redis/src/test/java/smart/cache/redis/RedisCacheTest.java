package smart.cache.redis;

import java.util.Collection;
import java.util.Set;
import org.junit.Test;
import smart.cache.ISmartCache;
import smart.cache.ISmartCacheManager;

public class RedisCacheTest {
    /**
     * 默认cacheManager.getCache("redis");  默认对应config.properties中的cache.redis.ip
     * cacheManager.getCache("xxx"); 对应config.properties中的cache.redis.ip.xxx
     *
     * */

    @Test
    public void test() {
        ISmartCacheManager cacheManager = new RedisCacheManager();
        ISmartCache<String, Object> cache = cacheManager.getCache("redis");
        System.out.println(cache.get("lu"));
        System.out.println(cache.put("lu","heihei"));
        System.out.println(cache.get("lu"));


    }

    @Test
    public void test2() {
        ISmartCacheManager cacheManager = new RedisCacheManager();
        ISmartCache<Object, Object> cache = cacheManager.getCache("one");


        System.out.println(cache.put(123,123));
        System.out.println(cache.get(123));

    }

    @Test
    public void testRemove(){
        ISmartCacheManager cacheManager = new RedisCacheManager();
        ISmartCache<Object, Object> cache = cacheManager.getCache("xxx");
        System.out.println(cache.get(123));
        System.out.println(cache.remove(123));
        System.out.println(cache.get(123));
    }

    @Test
    public void testClear(){
        ISmartCacheManager cacheManager = new RedisCacheManager();
        ISmartCache<Object, Object> cache = cacheManager.getCache("cache_name");
        System.out.println(cache.get(123));
        cache.clear();
        System.out.println(cache.get(123));
        System.out.println(cache.get("lu"));
    }

    @Test
    public void testSize(){
        ISmartCacheManager cacheManager = new RedisCacheManager();
        ISmartCache<Object, Object> cache = cacheManager.getCache("cache_name");
        System.out.println(cache.size());
    }

    @Test
    public void testKeys(){
        ISmartCacheManager cacheManager = new RedisCacheManager();
        ISmartCache<Object, Object> cache = cacheManager.getCache("cache_name");
        Set<Object> keys =cache.keys();
        for(Object obj:keys){
            System.out.println(obj.toString());
        }
    }

    @Test
    public void testValues(){
        ISmartCacheManager cacheManager = new RedisCacheManager();
        ISmartCache<Object, Object> cache = cacheManager.getCache("cache_name");
        Collection<Object> list =cache.values();
        for(Object obj:list){
            System.out.println(obj.toString());
        }
    }



//    @Test
//    public void listTest() {
//        List<String> list = new ArrayList<String>();
//        list.add("A");
//        list.add("B");
//        list.add("C");
//    }
}
