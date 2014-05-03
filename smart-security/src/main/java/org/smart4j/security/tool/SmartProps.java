package org.smart4j.security.tool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.security.SmartSecurity;

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

    public static DataSource getDataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(props.getProperty("jdbc.driver"));
        ds.setUrl(props.getProperty("jdbc.url"));
        ds.setUsername(props.getProperty("jdbc.username"));
        ds.setPassword(props.getProperty("jdbc.password"));
        return ds;
    }

    public static String getRealms() {
        return props.getProperty("security.realms");
    }

    public static SmartSecurity getSmartSecurity() {
        String className = props.getProperty("security.custom.class");
        Class<?> cls = null;
        try {
            cls = Class.forName(className);
        } catch (ClassNotFoundException e) {
            logger.error("无法从 security.custom.class 配置中找到对应的类", e);
        }
        SmartSecurity smartSecurity = null;
        if (cls != null) {
            try {
                smartSecurity = (SmartSecurity) cls.newInstance();
            } catch (Exception e) {
                logger.error("实例化 SmartSecurity 异常", e);
            }
        }
        return smartSecurity;
    }

    public static String getJdbcAuthcQuery() {
        return props.getProperty("security.jdbc.authc_query");
    }

    public static String getJdbcRolesQuery() {
        return props.getProperty("security.jdbc.roles_query");
    }

    public static String getJdbcPermsQuery() {
        return props.getProperty("security.jdbc.perms_query");
    }

    public static String getAdUrl() {
        return props.getProperty("security.ad.url");
    }

    public static String getAdSystemUsername() {
        return props.getProperty("security.ad.system_username");
    }

    public static String getAdSystemPassword() {
        return props.getProperty("security.ad.system_password");
    }

    public static String getAdSearchBase() {
        return props.getProperty("security.ad.search_base");
    }

    public static boolean isCache() {
        return Boolean.parseBoolean(props.getProperty("security.cache"));
    }
}
