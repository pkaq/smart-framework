package com.smart.security;

import com.smart.security.exception.LoginException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartSecurityHelper {

    private static final Logger logger = LoggerFactory.getLogger(SmartSecurityHelper.class);

    private static final PasswordService passwordService = new DefaultPasswordService();

    public static void login(String username, String password, boolean isRememberMe) throws LoginException {
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        token.setRememberMe(isRememberMe);
        Subject user = SecurityUtils.getSubject();
        try {
            user.login(token);
        } catch (AuthenticationException e) {
            logger.error("错误：登录失败！", e);
            throw new LoginException(e);
        }
    }

    public static void logout() {
        Subject user = SecurityUtils.getSubject();
        user.logout();
    }

    public static String encrypt(String plaintext) {
        return passwordService.encryptPassword(plaintext);
    }
}
