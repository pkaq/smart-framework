package com.smart.framework.helper;

import com.smart.framework.FrameworkConstant;
import com.smart.framework.Module;
import com.smart.framework.util.ClassUtil;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModuleHelper {

    private static final Logger logger = LoggerFactory.getLogger(ModuleHelper.class);

    // 创建一个 Module 列表（用于存放 Module 实例）
    private static final List<Module> moduleList = new ArrayList<Module>();

    static {
        try {
            // 获取并遍历所有的 Module 类（实现了 Module 接口的类）
            List<Class<?>> moduleClassList = ClassUtil.getClassListBySuper(FrameworkConstant.MODULE_PACKAGE, Module.class);
            for (Class<?> moduleClass : moduleClassList) {
                // 创建 Module 实例
                Module module = (Module) moduleClass.newInstance();
                // 将 Module 实例添加到 Module 列表中
                moduleList.add(module);
            }
        } catch (Exception e) {
            logger.error("初始化 ModuleHelper 出错！", e);
        }
    }

    public static List<Module> getModuleList() {
        return moduleList;
    }
}
