package com.smart.framework;

import com.smart.framework.helper.AOPHelper;
import com.smart.framework.helper.ActionHelper;
import com.smart.framework.helper.BeanHelper;
import com.smart.framework.helper.DBHelper;
import com.smart.framework.helper.EntityHelper;
import com.smart.framework.helper.IOCHelper;
import com.smart.framework.helper.PluginHelper;
import com.smart.framework.util.ClassUtil;

public final class Smart {

    public static void init() {
        Class<?>[] classList = {
            DBHelper.class,
            EntityHelper.class,
            ActionHelper.class,
            BeanHelper.class,
            AOPHelper.class,
            IOCHelper.class,
            PluginHelper.class,
        };
        for (Class<?> cls : classList) {
            ClassUtil.loadClass(cls.getName(), true);
        }
    }
}
