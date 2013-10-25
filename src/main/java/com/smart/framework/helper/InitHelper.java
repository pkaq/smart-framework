package com.smart.framework.helper;

import org.apache.log4j.Logger;

public class InitHelper {

    private static final Logger logger = Logger.getLogger(InitHelper.class);

    public static void init() {
        try {
            Class<?>[] classList = {
                DBHelper.class,
                EntityHelper.class,
                ActionHelper.class,
                BeanHelper.class,
                ServiceHelper.class,
                IOCHelper.class,
                AOPHelper.class,
            };
            for (Class<?> cls : classList) {
                Class.forName(cls.getName());
            }
        } catch (Exception e) {
            logger.error("加载 Helper 出错！", e);
        }
    }
}
