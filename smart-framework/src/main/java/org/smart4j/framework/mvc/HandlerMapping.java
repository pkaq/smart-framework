package org.smart4j.framework.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 处理器映射
 *
 * @author huangyong
 * @since 2.3
 */
public interface HandlerMapping {

    /**
     * 获取 Handler
     *
     * @param currentRequestMethod 当前请求方法
     * @param currentRequestPath   当前请求路径
     * @return Handler
     */
    Handler getHandler(String currentRequestMethod, String currentRequestPath);

    /**
     * 处理 Action 异常
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param e        异常
     */
    void handleActionException(HttpServletRequest request, HttpServletResponse response, Exception e);
}
