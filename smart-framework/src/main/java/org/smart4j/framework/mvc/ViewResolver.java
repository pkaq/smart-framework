package org.smart4j.framework.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 视图解析器
 *
 * @author huangyong
 * @since 2.3
 */
public interface ViewResolver {

    /**
     * 解析视图
     *
     * @param request            请求对象
     * @param response           响应对象
     * @param actionMethodResult Action 方法返回值
     */
    void resolveView(HttpServletRequest request, HttpServletResponse response, Object actionMethodResult);
}
