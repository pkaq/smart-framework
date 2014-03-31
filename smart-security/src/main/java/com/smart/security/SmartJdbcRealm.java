package com.smart.security;

import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.realm.jdbc.JdbcRealm;

public class SmartJdbcRealm extends JdbcRealm {

    public SmartJdbcRealm() {
        super.setDataSource(SmartProps.getDataSource());
        super.setAuthenticationQuery(SmartProps.getJdbcAuthcQuery());
        super.setUserRolesQuery(SmartProps.getJdbcRolesQuery());
        super.setPermissionsQuery(SmartProps.getJdbcPermsQuery());
        super.setPermissionsLookupEnabled(true);
        super.setCredentialsMatcher(new PasswordMatcher());
    }
}
