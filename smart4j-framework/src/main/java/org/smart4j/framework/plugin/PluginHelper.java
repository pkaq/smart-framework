package org.smart4j.framework.plugin;

import java.util.ArrayList;
import java.util.List;
import org.smart4j.framework.core.FrameworkConstant;
import org.smart4j.framework.core.fault.InitializationError;
import org.smart4j.framework.util.ClassUtil;

public class PluginHelper {

    // 创建一个 Plugin 列表（用于存放 Plugin 实例）
    private static final List<Plugin> pluginList = new ArrayList<Plugin>();

    static {
        try {
            // 获取并遍历所有的 Plugin 类（实现了 Plugin 接口的类）
            List<Class<?>> pluginClassList = ClassUtil.getClassListBySuper(FrameworkConstant.PLUGIN_PACKAGE, Plugin.class);
            for (Class<?> pluginClass : pluginClassList) {
                // 创建 Plugin 实例
                Plugin plugin = (Plugin) pluginClass.newInstance();
                // 调用初始化方法
                plugin.init();
                // 将 Plugin 实例添加到 Plugin 列表中
                pluginList.add(plugin);
            }
        } catch (Exception e) {
            throw new InitializationError("初始化 PluginHelper 出错！", e);
        }
    }

    public static List<Plugin> getPluginList() {
        return pluginList;
    }
}
