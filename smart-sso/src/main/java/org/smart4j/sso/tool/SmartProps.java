package org.smart4j.sso.tool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
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

    public static boolean isSSO() {
        return Boolean.parseBoolean(props.getProperty("smart.sso"));
    }

    public static String getCasServerUrlPrefix() {
        return props.getProperty("smart.sso.cas_url");
    }

    public static String getCasServerLoginUrl() {
        return props.getProperty("smart.sso.cas_url") + "/login";
    }

    public static String getServerName() {
        return props.getProperty("smart.sso.app_url");
    }

    public static String getFilterMapping() {
        return props.getProperty("smart.sso.filter_mapping");
    }
}
