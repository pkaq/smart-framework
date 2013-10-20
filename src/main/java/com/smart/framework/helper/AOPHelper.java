package com.smart.framework.helper;

import com.smart.framework.annotation.Aspect;
import com.smart.framework.base.BaseAspect;
import com.smart.framework.util.CollectionUtil;
import com.smart.framework.util.ObjectUtil;
import com.smart.framework.util.StringUtil;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import net.sf.cglib.proxy.Enhancer;
import org.apache.log4j.Logger;

public class AOPHelper {

    private static final Logger logger = Logger.getLogger(AOPHelper.class);

    static {
        if (logger.isDebugEnabled()) {
            logger.debug("初始化 AOPHelper");
        }

        try {
            // 获取并遍历所有的 Aspect 类（切面类）
            List<Class<?>> aspectClassList = ClassHelper.getClassListBySuper(BaseAspect.class);
            for (Class<?> aspectClass : aspectClassList) {
                // 判断 @Aspect 注解是否存在
                if (aspectClass.isAnnotationPresent(Aspect.class)) {
                    // 获取 @Aspect 注解中的属性值
                    Aspect aspect = aspectClass.getAnnotation(Aspect.class);
                    String pkg = aspect.pkg(); // 包名
                    String cls = aspect.cls(); // 类名
                    // 初始化目标类列表
                    List<Class<?>> targetClassList = new ArrayList<Class<?>>();
                    if (StringUtil.isNotEmpty(pkg) && StringUtil.isNotEmpty(cls)) {
                        // 如果包名与类名均不为空，则添加指定类
                        targetClassList.add(Class.forName(pkg + "." + cls));
                    } else {
                        // 否则（包名不为空）添加该包名下所有类
                        targetClassList.addAll(ClassHelper.getClassListByPackage(pkg));
                    }
                    // 遍历目标类列表
                    if (CollectionUtil.isNotEmpty(targetClassList)) {
                        // 创建父切面类
                        BaseAspect baseAspect = (BaseAspect) aspectClass.newInstance();
                        for (Class<?> targetClass : targetClassList) {
                            // 获取目标实例
                            Object targetInstance = BeanHelper.getBean(targetClass);
                            // 若目标实例不是代理实例，则创建代理实例
                            if (! Enhancer.isEnhanced(targetInstance.getClass())) {
                                // 创建代理实例
                                Object proxyInstance = baseAspect.getProxy(targetClass);
                                // 复制目标实例中的成员变量到代理实例中
                                ObjectUtil.copyFields(targetInstance, proxyInstance);
                                // 用代理实体覆盖目标实例
                                BeanHelper.getBeanMap().put(targetClass, proxyInstance);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("初始化 AOPHelper 出错！", e);
            throw new RuntimeException(e);
        }
    }
}
