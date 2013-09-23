package com.smart.framework.bean;

public class RequestBean {

    private String requestMethod;
    private String requestURL;

    public RequestBean(String requestMethod, String requestURL) {
        this.requestMethod = requestMethod;
        this.requestURL = requestURL;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }
}
