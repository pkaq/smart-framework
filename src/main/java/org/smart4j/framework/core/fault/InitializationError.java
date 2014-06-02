package org.smart4j.framework.core.fault;

/**
 * 初始化错误
 *
 * @author huangyong
 * @since 2.2
 */
public class InitializationError extends Error {

    public InitializationError() {
        super();
    }

    public InitializationError(String message) {
        super(message);
    }

    public InitializationError(String message, Throwable cause) {
        super(message, cause);
    }

    public InitializationError(Throwable cause) {
        super(cause);
    }
}
