package org.smart4j.plugin.security;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.plugin.security.fault.LoginException;

/**
 * Security 助手类
 *
 * @author huangyong
 * @since 2.3
 */
public final class SecurityHelper {

    private static final Logger logger = LoggerFactory.getLogger(SecurityHelper.class);

    /**
     * 登录
     */
    public static void login(String username, String password, boolean isRememberMe) throws LoginException {
        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser != null) {
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            token.setRememberMe(isRememberMe);
            try {
                currentUser.login(token);
            } catch (AuthenticationException e) {
                logger.error("错误：登录失败！", e);
                throw new LoginException(e);
            }
        }
    }

    /**
     * 注销
     */
    public static void logout() {
        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser != null) {
            currentUser.logout();
        }
    }
}
