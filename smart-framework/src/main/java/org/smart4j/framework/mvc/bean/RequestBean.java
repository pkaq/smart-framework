package org.smart4j.framework.mvc.bean;

/**
 * 用于封装 Request 相关信息
 *
 * @author huangyong
 * @since 1.0
 */
public class RequestBean {

    private String requestMethod;
    private String requestPath;

    public RequestBean(String requestMethod, String requestPath) {
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