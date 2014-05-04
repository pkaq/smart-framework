package org.smart4j.framework.mvc.fault;

/**
 * 访问异常（当非法访问时抛出）
 *
 * @author huangyong
 * @since 2.1
 */
public class AccessException extends RuntimeException {

    public AccessException() {
        super();
    }

    public AccessException(String message) {
        super(message);
    }

    public AccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessException(Throwable cause) {
        super(cause);
    }
}
