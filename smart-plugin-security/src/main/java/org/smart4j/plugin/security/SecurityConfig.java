package org.smart4j.plugin.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.framework.core.ConfigHelper;

/**
 * 从配置文件中获取相关属性
 *
 * @author huangyong
 * @since 2.3
 */
public final class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    public static String getRealms() {
        return ConfigHelper.getString(SecurityConstant.REALMS);
    }

    public static SmartSecurity getSmartSecurity() {
        String className = ConfigHelper.getString(SecurityConstant.SMART_SECURITY);
        Class<?> cls = null;
        try {
            cls = Class.forName(className);
        } catch (ClassNotFoundException e) {
            logger.error("无法从 " + SecurityConstant.SMART_SECURITY + " 配置中找到对应的类", e);
        }
        SmartSecurity smartSecurity = null;
        if (cls != null) {
            try {
                smartSecurity = (SmartSecurity) cls.newInstance();
            } catch (Exception e) {
                logger.error(SmartSecurity.class.getSimpleName() + " 实例化异常", e);
            }
        }
        return smartSecurity;
    }

    public static String getJdbcAuthcQuery() {
        return ConfigHelper.getString(SecurityConstant.JDBC_AUTHC_QUERY);
    }

    public static String getJdbcRolesQuery() {
        return ConfigHelper.getString(SecurityConstant.JDBC_ROLES_QUERY);
    }

    public static String getJdbcPermissionsQuery() {
        return ConfigHelper.getString(SecurityConstant.JDBC_PERMISSIONS_QUERY);
    }

    public static String getAdUrl() {
        return ConfigHelper.getString(SecurityConstant.AD_URL);
    }

    public static String getAdSystemUsername() {
        return ConfigHelper.getString(SecurityConstant.AD_SYSTEM_USERNAME);
    }

    public static String getAdSystemPassword() {
        return ConfigHelper.getString(SecurityConstant.AD_SYSTEM_PASSWORD);
    }

    public static String getAdSearchBase() {
        return ConfigHelper.getString(SecurityConstant.AD_SEARCH_BASE);
    }

    public static boolean isCacheable() {
        return ConfigHelper.getBoolean(SecurityConstant.CACHEABLE);
    }
}
