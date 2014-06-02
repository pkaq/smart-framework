package org.smart4j.plugin.security.realm;

import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.smart4j.framework.dao.DatabaseHelper;
import org.smart4j.plugin.security.SecurityConfig;

/**
 * 基于 Smart 的 JDBC Realm（需要提供相关 smart.security.jdbc.* 配置项）
 *
 * @author huangyong
 * @since 2.3
 */
public class SmartJdbcRealm extends JdbcRealm {

    public SmartJdbcRealm() {
        super.setDataSource(DatabaseHelper.getDataSource());
        super.setAuthenticationQuery(SecurityConfig.getJdbcAuthcQuery());
        super.setUserRolesQuery(SecurityConfig.getJdbcRolesQuery());
        super.setPermissionsQuery(SecurityConfig.getJdbcPermissionsQuery());
        super.setPermissionsLookupEnabled(true);
        super.setCredentialsMatcher(new PasswordMatcher());
    }
}
