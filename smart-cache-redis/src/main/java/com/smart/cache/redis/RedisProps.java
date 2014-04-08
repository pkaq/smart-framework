package com.smart.cache.redis;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RedisProps {

    private static final Logger logger = LoggerFactory.getLogger(RedisProps.class);

    private static final Properties redisProps = new Properties();

    static {
        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
            redisProps.load(is);
        } catch (IOException e) {
            logger.error("加载属性文件出错！", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    logger.error("释放资源出错！", e);
                }
            }
        }
    }

    public static String getDefaultIP(){
        return redisProps.getProperty("cache.redis.ip");
    }

    /**
     * 获取配置文件中 cache.redis.ip.xxx对应名字的属性
     * @param name
     * @return
     */
    public static String getIpByName(String name){
        if(name.equals("redis")){
            return getDefaultIP();
        }
        return redisProps.getProperty("cache.redis.ip."+name);
    }

}
