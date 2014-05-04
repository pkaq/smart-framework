package org.smart4j.framework.mvc.fault;

/**
 * 权限异常（当权限受限时抛出）
 *
 * @author huangyong
 * @since 2.1
 */
public class PermissionException extends RuntimeException {

    public PermissionException() {
        super();
    }

    public PermissionException(String message) {
        super(message);
    }

    public PermissionException(String message, Throwable cause) {
        super(message, cause);
    }

    public PermissionException(Throwable cause) {
        super(cause);
    }
}
