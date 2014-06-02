package org.smart4j.framework.mvc.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.smart4j.framework.FrameworkConstant;
import org.smart4j.framework.mvc.HandlerExceptionResolver;
import org.smart4j.framework.mvc.fault.AuthcException;
import org.smart4j.framework.mvc.fault.AuthzException;
import org.smart4j.framework.util.WebUtil;

/**
 * 默认 Handler 异常解析器
 *
 * @author huangyong
 * @since 2.3
 */
public class DefaultHandlerExceptionResolver implements HandlerExceptionResolver {

    @Override
    public void resolveHandlerException(HttpServletRequest request, HttpServletResponse response, Exception e) {
        // 判断异常原因
        Throwable cause = e.getCause();
        if (cause instanceof AuthcException) {
            // 分两种情况进行处理
            if (WebUtil.isAJAX(request)) {
                // 跳转到 403 页面
                WebUtil.sendError(HttpServletResponse.SC_FORBIDDEN, "", response);
            } else {
                // 重定向到首页
                WebUtil.redirectRequest(FrameworkConstant.HOME_PAGE, request, response);
            }
        } else if (cause instanceof AuthzException) {
            // 跳转到 403 页面
            WebUtil.sendError(HttpServletResponse.SC_FORBIDDEN, "", response);
        } else {
            // 跳转到 500 页面
            WebUtil.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, cause.getMessage(), response);
        }
    }
}
