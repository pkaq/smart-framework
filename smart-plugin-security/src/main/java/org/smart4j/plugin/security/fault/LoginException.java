package org.smart4j.plugin.security.fault;

/**
 * 登录异常
 *
 * @author huangyong
 * @since 2.3
 */
public class LoginException extends RuntimeException {

    public LoginException() {
        super();
    }

    public LoginException(String message) {
        super(message);
    }

    public LoginException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginException(Throwable cause) {
        super(cause);
    }

    public String getName() {
        return getClass().getSimpleName();
    }
}
