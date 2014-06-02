package org.smart4j.framework.plugin;

/**
 * 插件接口
 *
 * @author huangyong
 * @since 2.0
 */
public interface Plugin {

    /**
     * 初始化插件
     */
    void init();

    /**
     * 销毁插件
     */
    void destroy();
}
