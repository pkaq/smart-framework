package com.smart.framework;

import com.smart.framework.helper.ActionHelper;
import com.smart.framework.helper.AopHelper;
import com.smart.framework.helper.BeanHelper;
import com.smart.framework.helper.DatabaseHelper;
import com.smart.framework.helper.EntityHelper;
import com.smart.framework.helper.IocHelper;
import com.smart.framework.helper.PluginHelper;
import com.smart.framework.util.ClassUtil;

public final class HelperLoader {

    public static void init() {
        Class<?>[] classList = {
            DatabaseHelper.class,
            EntityHelper.class,
            ActionHelper.class,
            BeanHelper.class,
            AopHelper.class,
            IocHelper.class,
            PluginHelper.class,
        };
        for (Class<?> cls : classList) {
            ClassUtil.loadClass(cls.getName(), true);
        }
    }
}
