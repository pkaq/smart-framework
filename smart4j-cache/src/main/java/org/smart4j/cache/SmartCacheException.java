package org.smart4j.cache;

public class SmartCacheException extends RuntimeException {

    public SmartCacheException() {
        super();
    }

    public SmartCacheException(String message) {
        super(message);
    }

    public SmartCacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public SmartCacheException(Throwable cause) {
        super(cause);
    }
}