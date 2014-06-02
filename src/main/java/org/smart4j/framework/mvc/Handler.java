package org.smart4j.framework.mvc;

import java.lang.reflect.Method;
import java.util.regex.Matcher;

/**
 * 封装 Action 方法相关信息
 *
 * @author huangyong
 * @since 1.0
 */
public class Handler {

    private Class<?> actionClass;
    private Method actionMethod;
    private Matcher requestPathMatcher;

    public Handler(Class<?> actionClass, Method actionMethod) {
        this.actionClass = actionClass;
        this.actionMethod = actionMethod;
    }

    public Class<?> getActionClass() {
        return actionClass;
    }

    public Method getActionMethod() {
        return actionMethod;
    }

    public Matcher getRequestPathMatcher() {
        return requestPathMatcher;
    }

    public void setRequestPathMatcher(Matcher requestPathMatcher) {
        this.requestPathMatcher = requestPathMatcher;
    }
}