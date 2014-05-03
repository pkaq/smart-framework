package org.smart4j.framework.mvc;

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
}
