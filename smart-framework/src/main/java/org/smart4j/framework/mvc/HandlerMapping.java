package org.smart4j.framework.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.smart4j.framework.mvc.bean.ActionBean;

/**
 * 处理器映射
 *
 * @author huangyong
 * @since 2.3
 */
public interface HandlerMapping {

    /**
     * 获取 Action
     *
     * @param currentRequestMethod 当前请求方法
     * @param currentRequestPath   当前请求路径
     * @return ActionBean
     */
    ActionBean getAction(String currentRequestMethod, String currentRequestPath);

    /**
     * 调用 Action 方法
     *
     * @param request    请求对象
     * @param response   响应对象
     * @param actionBean ActionBean
     * @throws Exception 异常
     */
    void invokeActionMethod(HttpServletRequest request, HttpServletResponse response, ActionBean actionBean) throws Exception;

    /**
     * 处理 Action 异常
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param e        异常
     */
    void handleActionException(HttpServletRequest request, HttpServletResponse response, Exception e);
}
