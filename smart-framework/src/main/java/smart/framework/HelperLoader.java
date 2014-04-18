package smart.framework;

import smart.framework.helper.ActionHelper;
import smart.framework.helper.AopHelper;
import smart.framework.helper.BeanHelper;
import smart.framework.helper.DatabaseHelper;
import smart.framework.helper.EntityHelper;
import smart.framework.helper.IocHelper;
import smart.framework.helper.PluginHelper;
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
