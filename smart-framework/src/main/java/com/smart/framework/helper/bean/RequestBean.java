package com.smart.framework.helper.bean;

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