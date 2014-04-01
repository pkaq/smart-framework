package com.smart.security.tool;

import com.smart.security.ISmartSecurity;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartProps {

    private static final Logger logger = LoggerFactory.getLogger(SmartProps.class);

    private static final Properties smartProps = new Properties();

    static {
        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream("smart.properties");
            smartProps.load(is);
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

    public static DataSource getDataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(smartProps.getProperty("jdbc.driver"));
        ds.setUrl(smartProps.getProperty("jdbc.url"));
        ds.setUsername(smartProps.getProperty("jdbc.username"));
        ds.setPassword(smartProps.getProperty("jdbc.password"));
        return ds;
    }

    public static String getRealms() {
        return smartProps.getProperty("security.realms");
    }

    public static ISmartSecurity getSmartSecurity() {
        String className = smartProps.getProperty("security.custom.class");
        Class<?> cls = null;
        try {
            cls = Class.forName(className);
        } catch (ClassNotFoundException e) {
            logger.error("无法从 security.custom.class 配置中找到对应的类", e);
        }
        ISmartSecurity smartSecurity = null;
        if (cls != null) {
            try {
                smartSecurity = (ISmartSecurity) cls.newInstance();
            } catch (Exception e) {
                logger.error("实例化 SmartSecurity 异常", e);
            }
        }
        return smartSecurity;
    }

    public static String getJdbcAuthcQuery() {
        return smartProps.getProperty("security.jdbc.authc_query");
    }

    public static String getJdbcRolesQuery() {
        return smartProps.getProperty("security.jdbc.roles_query");
    }

    public static String getJdbcPermsQuery() {
        return smartProps.getProperty("security.jdbc.perms_query");
    }

    public static String getAdUrl() {
        return smartProps.getProperty("security.ad.url");
    }

    public static String getAdSystemUsername() {
        return smartProps.getProperty("security.ad.system_username");
    }

    public static String getAdSystemPassword() {
        return smartProps.getProperty("security.ad.system_password");
    }

    public static String getAdSearchBase() {
        return smartProps.getProperty("security.ad.search_base");
    }

    public static boolean isCache() {
        return Boolean.parseBoolean(smartProps.getProperty("security.cache"));
    }
}
