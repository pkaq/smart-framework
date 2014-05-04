package org.smart4j.framework.mvc.fault;

/**
 * 上传异常（当文件上传失败时抛出）
 *
 * @author huangyong
 * @since 2.1
 */
public class UploadException extends RuntimeException {

    public UploadException() {
        super();
    }

    public UploadException(String message) {
        super(message);
    }

    public UploadException(String message, Throwable cause) {
        super(message, cause);
    }

    public UploadException(Throwable cause) {
        super(cause);
    }
}
