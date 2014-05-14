package org.smart4j.framework.plugin;

import java.util.ArrayList;
import java.util.List;
import org.smart4j.framework.FrameworkConstant;
import org.smart4j.framework.InstanceFactory;
import org.smart4j.framework.core.ClassScanner;
import org.smart4j.framework.core.fault.InitializationError;

/**
 * 初始化插件
 *
 * @author huangyong
 * @since 2.0
 */
public class PluginHelper {

    /**
     * 创建一个插件列表（用于存放插件实例）
     */
    private static final List<Plugin> pluginList = new ArrayList<Plugin>();

    /**
     * 获取 ClassScanner
     */
    private static final ClassScanner classScanner = InstanceFactory.getClassScanner();

    static {
        try {
            // 获取并遍历所有的插件类（实现了 Plugin 接口的类）
            List<Class<?>> pluginClassList = classScanner.getClassListBySuper(FrameworkConstant.PLUGIN_PACKAGE, Plugin.class);
            for (Class<?> pluginClass : pluginClassList) {
                // 创建插件实例
                Plugin plugin = (Plugin) pluginClass.newInstance();
                // 调用初始化方法
                plugin.init();
                // 将插件实例添加到插件列表中
                pluginList.add(plugin);
            }
        } catch (Exception e) {
            throw new InitializationError("初始化 PluginHelper 出错！", e);
        }
    }

    /**
     * 获取所有插件
     */
    public static List<Plugin> getPluginList() {
        return pluginList;
    }
}
