package com.smart.cache.redis;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import redis.clients.jedis.Jedis;

public class RedisTest {

    private static Jedis jedis;

    @BeforeClass
    public static void init() {
        jedis = new Jedis("localhost");
    }

    @Test
    public void stringTest() {
        jedis.set("foo", "bar");
        String value = jedis.get("foo");
        Assert.assertEquals("bar", value);
    }

//    @Test
//    public void listTest() {
//        List<String> list = new ArrayList<String>();
//        list.add("A");
//        list.add("B");
//        list.add("C");
//    }
}
