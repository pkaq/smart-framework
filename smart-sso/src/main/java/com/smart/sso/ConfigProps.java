package com.smart.sso;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigProps {

    private static final Logger logger = LoggerFactory.getLogger(ConfigProps.class);

    private static final Properties configProps = new Properties();

    static {
        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
            configProps.load(is);
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

    public static boolean isSSO() {
        return Boolean.parseBoolean(configProps.getProperty("sso"));
    }

    public static String getCasServerUrlPrefix() {
        return configProps.getProperty("sso.cas_url");
    }

    public static String getCasServerLoginUrl() {
        return configProps.getProperty("sso.cas_url") + "/login";
    }

    public static String getServerName() {
        return configProps.getProperty("sso.app_url");
    }

    public static String getFilterMapping() {
        return configProps.getProperty("sso.filter_mapping");
    }
}
