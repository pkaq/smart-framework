package org.smart4j.framework.mvc.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.smart4j.framework.core.FrameworkConstant;
import org.smart4j.framework.mvc.ActionHelper;
import org.smart4j.framework.mvc.Handler;
import org.smart4j.framework.mvc.HandlerMapping;
import org.smart4j.framework.mvc.Requestor;
import org.smart4j.framework.mvc.fault.AccessException;
import org.smart4j.framework.mvc.fault.PermissionException;
import org.smart4j.framework.util.WebUtil;

/**
 * 默认处理器映射
 *
 * @author huangyong
 * @since 2.3
 */
public class DefaultHandlerMapping implements HandlerMapping {

    // 用于缓存 Handler 实例
    private static final Map<String, Handler> cache = new ConcurrentHashMap<String, Handler>();

    @Override
    public Handler getHandler(String currentRequestMethod, String currentRequestPath) {
        // 若缓存中存在对应的实例，则返回该实例
        String cacheKey = currentRequestMethod + ":" + currentRequestPath;
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }
        // 定义一个 Handler
        Handler handler = null;
        // 获取并遍历 Action 映射
        Map<Requestor, Handler> actionMap = ActionHelper.getActionMap();
        for (Map.Entry<Requestor, Handler> actionEntry : actionMap.entrySet()) {
            // 从 Requestor 中获取 Request 相关属性
            Requestor requestor = actionEntry.getKey();
            String requestMethod = requestor.getRequestMethod();
            String requestPath = requestor.getRequestPath(); // 正则表达式
            // 获取请求路径匹配器（使用正则表达式匹配请求路径并从中获取相应的请求参数）
            Matcher requestPathMatcher = Pattern.compile(requestPath).matcher(currentRequestPath);
            // 判断请求方法与请求路径是否同时匹配
            if (requestMethod.equalsIgnoreCase(currentRequestMethod) && requestPathMatcher.matches()) {
                // 获取 Handler 及其相关属性
                handler = actionEntry.getValue();
                // 设置请求路径匹配器
                if (handler != null) {
                    handler.setRequestPathMatcher(requestPathMatcher);
                }
                // 若成功匹配，则终止循环
                break;
            }
        }
        // 若该实例不为空，则将其放入缓存
        if (handler != null) {
            cache.put(cacheKey, handler);
        }
        // 返回该 Handler
        return handler;
    }

    @Override
    public void handleActionException(HttpServletRequest request, HttpServletResponse response, Exception e) {
        // 判断异常原因
        Throwable cause = e.getCause();
        if (cause instanceof AccessException) {
            // 分两种情况进行处理
            if (WebUtil.isAJAX(request)) {
                // 跳转到 403 页面
                WebUtil.sendError(HttpServletResponse.SC_FORBIDDEN, "", response);
            } else {
                // 重定向到首页
                WebUtil.redirectRequest(FrameworkConstant.HOME_PAGE, request, response);
            }
        } else if (cause instanceof PermissionException) {
            // 跳转到 403 页面
            WebUtil.sendError(HttpServletResponse.SC_FORBIDDEN, "", response);
        } else {
            // 跳转到 500 页面
            WebUtil.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, cause.getMessage(), response);
        }
    }
}
