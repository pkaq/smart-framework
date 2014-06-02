package org.smart4j.framework.aop;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.smart4j.framework.FrameworkConstant;
import org.smart4j.framework.InstanceFactory;
import org.smart4j.framework.aop.annotation.Aspect;
import org.smart4j.framework.aop.annotation.AspectOrder;
import org.smart4j.framework.aop.proxy.Proxy;
import org.smart4j.framework.aop.proxy.ProxyManager;
import org.smart4j.framework.core.ClassHelper;
import org.smart4j.framework.core.ClassScanner;
import org.smart4j.framework.core.fault.InitializationError;
import org.smart4j.framework.ioc.BeanHelper;
import org.smart4j.framework.plugin.PluginProxy;
import org.smart4j.framework.tx.TransactionProxy;
import org.smart4j.framework.tx.annotation.Service;
import org.smart4j.framework.util.ClassUtil;
import org.smart4j.framework.util.CollectionUtil;
import org.smart4j.framework.util.StringUtil;

/**
 * 初始化 AOP 框架
 *
 * @author huangyong
 * @since 1.0
 */
public class AopHelper {

    /**
     * 获取 ClassScanner
     */
    private static final ClassScanner classScanner = InstanceFactory.getClassScanner();

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
                // 用代理实例覆盖目标实例，并放入 Bean 容器中
                BeanHelper.setBean(targetClass, proxyInstance);
            }
        } catch (Exception e) {
            throw new InitializationError("初始化 AopHelper 出错！", e);
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
        List<Class<?>> pluginProxyClassList = classScanner.getClassListBySuper(FrameworkConstant.PLUGIN_PACKAGE, PluginProxy.class);
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
        // 添加插件包下所有的切面类
        aspectProxyClassList.addAll(classScanner.getClassListBySuper(FrameworkConstant.PLUGIN_PACKAGE, AspectProxy.class));
        // 排序切面类
        sortAspectProxyClassList(aspectProxyClassList);
        // 遍历切面类
        for (Class<?> aspectProxyClass : aspectProxyClassList) {
            // 判断 Aspect 注解是否存在
            if (aspectProxyClass.isAnnotationPresent(Aspect.class)) {
                // 获取 Aspect 注解
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
                if (aspect1.isAnnotationPresent(AspectOrder.class) || aspect2.isAnnotationPresent(AspectOrder.class)) {
                    // 若有 Order 注解，则优先比较（序号的值越小越靠前）
                    if (aspect1.isAnnotationPresent(AspectOrder.class)) {
                        return getOrderValue(aspect1) - getOrderValue(aspect2);
                    } else {
                        return getOrderValue(aspect2) - getOrderValue(aspect1);
                    }
                } else {
                    // 若无 Order 注解，则比较类名（按字母顺序升序排列）
                    return aspect1.hashCode() - aspect2.hashCode();
                }
            }

            private int getOrderValue(Class<?> aspect) {
                return aspect.getAnnotation(AspectOrder.class) != null ? aspect.getAnnotation(AspectOrder.class).value() : 0;
            }
        });
    }

    private static List<Class<?>> createTargetClassList(Aspect aspect) throws Exception {
        List<Class<?>> targetClassList = new ArrayList<Class<?>>();
        // 获取 Aspect 注解的相关属性
        String pkg = aspect.pkg();
        String cls = aspect.cls();
        Class<? extends Annotation> annotation = aspect.annotation();
        // 若包名不为空，则需进一步判断类名是否为空
        if (StringUtil.isNotEmpty(pkg)) {
            if (StringUtil.isNotEmpty(cls)) {
                // 若类名不为空，则仅添加该类
                targetClassList.add(ClassUtil.loadClass(pkg + "." + cls, false));
            } else {
                // 若注解不为空且不是 Aspect 注解，则添加指定包名下带有该注解的所有类
                if (annotation != null && !annotation.equals(Aspect.class)) {
                    targetClassList.addAll(classScanner.getClassListByAnnotation(pkg, annotation));
                } else {
                    // 否则添加该包名下所有类
                    targetClassList.addAll(classScanner.getClassList(pkg));
                }
            }
        } else {
            // 若注解不为空且不是 Aspect 注解，则添加应用包名下带有该注解的所有类
            if (annotation != null && !annotation.equals(Aspect.class)) {
                targetClassList.addAll(ClassHelper.getClassListByAnnotation(annotation));
            }
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
