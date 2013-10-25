package com.smart.framework.helper;

import com.smart.framework.annotation.Aspect;
import com.smart.framework.base.BaseAspect;
import com.smart.framework.proxy.Proxy;
import com.smart.framework.proxy.ProxyFactory;
import com.smart.framework.util.ObjectUtil;
import com.smart.framework.util.StringUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class AOPHelper {

    private static final Logger logger = Logger.getLogger(AOPHelper.class);

    static {
        if (logger.isDebugEnabled()) {
            logger.debug("初始化 AOPHelper");
        }
        try {
            // 创建 Aspect Map（用于存放切面类与目标类列表的映射关系）
            Map<Class<?>, List<Class<?>>> aspectMap = createAspectMap();
            // 创建 Target Map（用于存放目标类与代理类列表 的映射关系）
            Map<Class<?>, List<Proxy>> targetMap = createTargetMap(aspectMap);
            // 遍历 Target Map
            for (Map.Entry<Class<?>, List<Proxy>> targetEntry : targetMap.entrySet()) {
                // 分别获取 map 中的 key 与 value
                Class<?> targetClass = targetEntry.getKey();
                List<Proxy> baseAspectList = targetEntry.getValue();
                // 创建代理实例
                Object proxyInstance = new ProxyFactory(targetClass, baseAspectList).createProxy();
                // 获取目标实例（从 IOC 容器中获取）
                Object targetInstance = BeanHelper.getBean(targetClass);
                // 复制目标实例中的成员变量到代理实例中
                ObjectUtil.copyFields(targetInstance, proxyInstance);
                // 用代理实例覆盖目标实例（放入 IOC 容器中）
                BeanHelper.getBeanMap().put(targetClass, proxyInstance);
            }
        } catch (Exception e) {
            logger.error("初始化 AOPHelper 出错！", e);
        }
    }

    private static Map<Class<?>, List<Class<?>>> createAspectMap() throws Exception {
        // 定义 Aspect Map
        Map<Class<?>, List<Class<?>>> aspectMap = new HashMap<Class<?>, List<Class<?>>>();
        // 获取并遍历所有的切面类
        List<Class<?>> aspectClassList = ClassHelper.getClassListBySuper(BaseAspect.class);
        for (Class<?> aspectClass : aspectClassList) {
            // 判断 @Aspect 注解是否存在
            if (aspectClass.isAnnotationPresent(Aspect.class)) {
                // 获取 @Aspect 注解
                Aspect aspect = aspectClass.getAnnotation(Aspect.class);
                // 创建目标类列表
                List<Class<?>> targetClassList = createTargetClassList(aspect);
                // 初始化 Aspect Map
                aspectMap.put(aspectClass, targetClassList);
            }
        }
        // 返回 Aspect Map
        return aspectMap;
    }

    private static List<Class<?>> createTargetClassList(Aspect aspect) throws Exception {
        // 初始化目标类列表
        List<Class<?>> targetClassList = new ArrayList<Class<?>>();
        // 获取 @Aspect 注解相关属性
        String pkg = aspect.pkg(); // 包名
        String cls = aspect.cls(); // 类名
        if (StringUtil.isNotEmpty(pkg) && StringUtil.isNotEmpty(cls)) {
            // 如果包名与类名均不为空，则添加指定类
            targetClassList.add(Class.forName(pkg + "." + cls));
        } else {
            // 否则（包名不为空）添加该包名下所有类
            targetClassList.addAll(ClassHelper.getClassListByPackage(pkg));
        }
        // 返回目标类列表
        return targetClassList;
    }

    private static Map<Class<?>, List<Proxy>> createTargetMap(Map<Class<?>, List<Class<?>>> aspectMap) throws Exception {
        // 定义 Target Map
        Map<Class<?>, List<Proxy>> targetMap = new HashMap<Class<?>, List<Proxy>>();
        // 遍历 Aspect Map
        for (Map.Entry<Class<?>, List<Class<?>>> aspectEntry : aspectMap.entrySet()) {
            // 分别获取 map 中的 key 与 value
            Class<?> aspectClass = aspectEntry.getKey();
            List<Class<?>> targetClassList = aspectEntry.getValue();
            // 遍历目标类列表
            for (Class<?> targetClass : targetClassList) {
                // 创建代理类（切面类）实例
                Proxy baseAspect = (Proxy) aspectClass.newInstance();
                // 初始化 Aspect Map
                if (targetMap.containsKey(targetClass)) {
                    targetMap.get(targetClass).add(baseAspect);
                } else {
                    List<Proxy> baseAspectList = new ArrayList<Proxy>();
                    baseAspectList.add(baseAspect);
                    targetMap.put(targetClass, baseAspectList);
                }
            }
        }
        // 返回 Target Map
        return targetMap;
    }
}
