package com.smart.framework.helper;

import com.smart.framework.FrameworkConstant;
import com.smart.framework.annotation.Aspect;
import com.smart.framework.annotation.Order;
import com.smart.framework.annotation.Service;
import com.smart.framework.proxy.AspectProxy;
import com.smart.framework.proxy.PluginProxy;
import com.smart.framework.proxy.Proxy;
import com.smart.framework.proxy.ProxyManager;
import com.smart.framework.proxy.TransactionProxy;
import com.smart.framework.util.ClassUtil;
import com.smart.framework.util.CollectionUtil;
import com.smart.framework.util.StringUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AOPHelper {

    private static final Logger logger = LoggerFactory.getLogger(AOPHelper.class);

    static {
        try {
            // 创建 Proxy Map（用于 存放代理类 与 目标类列表 的映射关系）
            Map<Class<?>, List<Class<?>>> proxyMap = createProxyMap();
            // 创建 Target Map（用于 存放目标类 与 代理类列表 的映射关系）
            Map<Class<?>, List<Proxy>> targetMap = createTargetMap(proxyMap);
            // 遍历 Target Map
            for (Map.Entry<Class<?>, List<Proxy>> targetEntry : targetMap.entrySet()) {
                // 分别获取 map 中的 key 与 value
                Class<?> targetClass = targetEntry.getKey();
                List<Proxy> proxyList = targetEntry.getValue();
                // 创建代理实例
                Object proxyInstance = ProxyManager.createProxy(targetClass, proxyList);
                // 用代理实例覆盖目标实例，并放入 IOC 容器中
                BeanHelper.setBean(targetClass, proxyInstance);
            }
        } catch (Exception e) {
            logger.error("初始化 AOPHelper 出错！", e);
        }
    }

    private static Map<Class<?>, List<Class<?>>> createProxyMap() throws Exception {
        Map<Class<?>, List<Class<?>>> proxyMap = new LinkedHashMap<Class<?>, List<Class<?>>>();
        // 添加相关代理
        addPluginProxy(proxyMap);      // 插件代理
        addAspectProxy(proxyMap);      // 切面代理
        addTransactionProxy(proxyMap); // 事务代理
        return proxyMap;
    }

    private static void addPluginProxy(Map<Class<?>, List<Class<?>>> proxyMap) throws Exception {
        // 获取插件包名下父类为 PluginProxy 的所有类（插件代理类）
        List<Class<?>> pluginProxyClassList = ClassUtil.getClassListBySuper(FrameworkConstant.PLUGIN_PACKAGE, PluginProxy.class);
        if (CollectionUtil.isNotEmpty(pluginProxyClassList)) {
            // 遍历所有插件代理类
            for (Class<?> pluginProxyClass : pluginProxyClassList) {
                // 创建插件代理类实例
                PluginProxy pluginProxy = (PluginProxy) pluginProxyClass.newInstance();
                // 将插件代理类及其所对应的目标类列表放入 Proxy Map 中
                proxyMap.put(pluginProxyClass, pluginProxy.getTargetClassList());
            }
        }
    }

    private static void addAspectProxy(Map<Class<?>, List<Class<?>>> proxyMap) throws Exception {
        // 获取切面类（所有继承于 BaseAspect 的类）
        List<Class<?>> aspectProxyClassList = ClassHelper.getClassListBySuper(AspectProxy.class);
        // 排序切面类
        sortAspectProxyClassList(aspectProxyClassList);
        // 遍历切面类
        for (Class<?> aspectProxyClass : aspectProxyClassList) {
            // 判断 @Aspect 注解是否存在
            if (aspectProxyClass.isAnnotationPresent(Aspect.class)) {
                // 获取 @Aspect 注解
                Aspect aspect = aspectProxyClass.getAnnotation(Aspect.class);
                // 创建目标类列表
                List<Class<?>> targetClassList = createTargetClassList(aspect);
                // 初始化 Proxy Map
                proxyMap.put(aspectProxyClass, targetClassList);
            }
        }
    }

    private static void addTransactionProxy(Map<Class<?>, List<Class<?>>> proxyMap) {
        // 使用 TransactionProxy 代理所有 Service 类
        List<Class<?>> serviceClassList = ClassHelper.getClassListByAnnotation(Service.class);
        proxyMap.put(TransactionProxy.class, serviceClassList);
    }

    private static void sortAspectProxyClassList(List<Class<?>> proxyClassList) {
        // 排序代理类列表
        Collections.sort(proxyClassList, new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> aspect1, Class<?> aspect2) {
                if (aspect1.isAnnotationPresent(Order.class) || aspect2.isAnnotationPresent(Order.class)) {
                    // 若有 @Order 注解，则优先比较（序号的值越小越靠前）
                    if (aspect1.isAnnotationPresent(Order.class)) {
                        return getOrderValue(aspect1) - getOrderValue(aspect2);
                    } else {
                        return getOrderValue(aspect2) - getOrderValue(aspect1);
                    }
                } else {
                    // 若无 @Order 注解，则比较类名（按字母顺序升序排列）
                    return aspect1.hashCode() - aspect2.hashCode();
                }
            }

            private int getOrderValue(Class<?> aspect) {
                return aspect.getAnnotation(Order.class) != null ? aspect.getAnnotation(Order.class).value() : 0;
            }
        });
    }

    private static List<Class<?>> createTargetClassList(Aspect aspect) throws Exception {
        List<Class<?>> targetClassList = new ArrayList<Class<?>>();
        // 获取 @Aspect 注解相关属性
        String pkg = aspect.pkg(); // 包名
        String cls = aspect.cls(); // 类名
        if (StringUtil.isNotEmpty(pkg) && StringUtil.isNotEmpty(cls)) {
            // 如果包名与类名均不为空，则添加指定类
            targetClassList.add(Class.forName(pkg + "." + cls));
        } else {
            // 否则（包名不为空）添加该包名下所有类
            targetClassList.addAll(ClassUtil.getClassList(pkg, true));
        }
        return targetClassList;
    }

    private static Map<Class<?>, List<Proxy>> createTargetMap(Map<Class<?>, List<Class<?>>> proxyMap) throws Exception {
        Map<Class<?>, List<Proxy>> targetMap = new HashMap<Class<?>, List<Proxy>>();
        // 遍历 Proxy Map
        for (Map.Entry<Class<?>, List<Class<?>>> proxyEntry : proxyMap.entrySet()) {
            // 分别获取 map 中的 key 与 value
            Class<?> proxyClass = proxyEntry.getKey();
            List<Class<?>> targetClassList = proxyEntry.getValue();
            // 遍历目标类列表
            for (Class<?> targetClass : targetClassList) {
                // 创建代理类（切面类）实例
                Proxy baseAspect = (Proxy) proxyClass.newInstance();
                // 初始化 Target Map
                if (targetMap.containsKey(targetClass)) {
                    targetMap.get(targetClass).add(baseAspect);
                } else {
                    List<Proxy> baseAspectList = new ArrayList<Proxy>();
                    baseAspectList.add(baseAspect);
                    targetMap.put(targetClass, baseAspectList);
                }
            }
        }
        return targetMap;
    }
}
