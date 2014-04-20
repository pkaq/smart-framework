package org.smart4j.security.realm;

import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.smart4j.security.tool.SmartProps;

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
