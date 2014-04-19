package smart.framework.core;

import smart.framework.aop.AopHelper;
import smart.framework.dao.DatabaseHelper;
import smart.framework.ioc.BeanHelper;
import smart.framework.ioc.IocHelper;
import smart.framework.mvc.ActionHelper;
import smart.framework.orm.EntityHelper;
import smart.framework.plugin.PluginHelper;
import smart.framework.util.ClassUtil;

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
