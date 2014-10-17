package org.smart4j.framework.mvc;

/**
 * 封装 Request 对象相关信息
 *
 * @author huangyong
 * @since 1.0
 */
public class Requester {

    private String requestMethod;
    private String requestPath;

    public Requester(String requestMethod, String requestPath) {
        this.requestMethod = requestMethod;
        this.requestPath = requestPath;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestPath() {
        return requestPath;
    }
}