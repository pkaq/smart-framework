package org.smart4j.plugin.security;

/**
 * 常量接口
 *
 * @author huangyong
 * @since 2.3
 */
public interface SecurityConstant {

    String REALMS = "smart.security.realms";
    String REALMS_JDBC = "jdbc";
    String REALMS_AD = "ad";
    String REALMS_CUSTOM = "custom";

    String SMART_SECURITY = "smart.security.custom.class";

    String JDBC_AUTHC_QUERY = "smart.security.jdbc.authc_query";
    String JDBC_ROLES_QUERY = "smart.security.jdbc.roles_query";
    String JDBC_PERMISSIONS_QUERY = "smart.security.jdbc.permissions_query";

    String AD_URL = "smart.security.ad.url";
    String AD_SYSTEM_USERNAME = "smart.security.ad.system_username";
    String AD_SYSTEM_PASSWORD = "smart.security.ad.system_password";
    String AD_SEARCH_BASE = "smart.security.ad.search_base";

    String CACHEABLE = "smart.security.cacheable";
}
