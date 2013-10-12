package com.smart.framework.helper;

import com.smart.framework.TransactionProxy;
import com.smart.framework.base.BaseService;
import com.smart.framework.util.ObjectUtil;
import java.util.List;
import org.apache.log4j.Logger;

public class ServiceHelper {

    private static final Logger logger = Logger.getLogger(ServiceHelper.class);

    static {
        if (logger.isInfoEnabled()) {
            logger.info("Init ServiceHelper...");
        }

        try {
            // 获取并遍历所有的 Service 类
            List<Class<?>> serviceClassList = ClassHelper.getClassListBySuper(BaseService.class);
            for (Class<?> serviceClass : serviceClassList) {
                // 获取目标实例
                Object targetInstance = BeanHelper.getBean(serviceClass);
                // 创建代理实例
                Object proxyInstance = TransactionProxy.getInstance().getProxy(serviceClass);
                // 复制目标实例中的字段到代理实例中
                ObjectUtil.copyFields(targetInstance, proxyInstance);
                // 用代理实例覆盖目标实例
                BeanHelper.getBeanMap().put(serviceClass, proxyInstance);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
