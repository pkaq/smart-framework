package org.smart4j.plugin.security.realm;

import java.util.HashSet;
import java.util.Set;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.smart4j.plugin.security.SmartSecurity;

/**
 * 基于 Smart 的自定义 Realm（需要实现 SmartSecurity 接口）
 *
 * @author huangyong
 * @since 2.3
 */
public class SmartCustomRealm extends AuthorizingRealm {

    private final SmartSecurity smartSecurity;

    public SmartCustomRealm(SmartSecurity smartSecurity) {
        this.smartSecurity = smartSecurity;
        super.setName("custom");
        super.setCredentialsMatcher(new PasswordMatcher());
    }

    @Override
    public AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if (token == null) {
            throw new AuthenticationException("参数 token 非法！");
        }

        String username = ((UsernamePasswordToken) token).getUsername();

        String password = smartSecurity.getPassword(username);

        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo();
        authenticationInfo.setPrincipals(new SimplePrincipalCollection(username, super.getName()));
        authenticationInfo.setCredentials(password);
        return authenticationInfo;
    }

    @Override
    public AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        if (principals == null) {
            throw new AuthorizationException("参数 principals 非法！");
        }

        String username = (String) super.getAvailablePrincipal(principals);

        Set<String> roleNameSet = smartSecurity.getRoleNameSet(username);

        Set<String> permNameSet = new HashSet<String>();
        if (roleNameSet != null && roleNameSet.size() > 0) {
            for (String roleName : roleNameSet) {
                Set<String> currentPermNameSet = smartSecurity.getPermissionNameSet(roleName);
                permNameSet.addAll(currentPermNameSet);
            }
        }

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setRoles(roleNameSet);
        authorizationInfo.setStringPermissions(permNameSet);
        return authorizationInfo;
    }
}
