package com.smart.cache.redis;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartProps {

    private static final Logger logger = LoggerFactory.getLogger(SmartProps.class);

    private static final Properties props = new Properties();

    static {
        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream("smart.properties");
            props.load(is);
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

    public static String getHost() {
        String host = props.getProperty("cache.redis.host");
        if (StringUtils.isEmpty(host)) {
            host = "127.0.0.1";
        }
        return host;
    }

    public static int getPort() {
        int port;
        try {
            port = Integer.parseInt(props.getProperty("cache.redis.port"));
        } catch (NumberFormatException e) {
            port = 6379;
        }
        return port;
    }
}
