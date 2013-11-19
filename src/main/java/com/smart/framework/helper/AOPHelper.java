package com.smart.framework.helper;

import com.smart.framework.annotation.Aspect;
import com.smart.framework.annotation.Order;
import com.smart.framework.aspect.PluginAspect;
import com.smart.framework.aspect.TransactionAspect;
import com.smart.framework.base.BaseAspect;
import com.smart.framework.base.BaseService;
import com.smart.framework.proxy.Proxy;
import com.smart.framework.proxy.ProxyManager;
import com.smart.framework.util.ClassUtil;
import com.smart.framework.util.CollectionUtil;
import com.smart.framework.util.ObjectUtil;
import com.smart.framework.util.StringUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class AOPHelper {

    private static final Logger logger = Logger.getLogger(AOPHelper.class);
    private static final AOPHelper instance = new AOPHelper();

    private AOPHelper() {
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
                Object proxyInstance = ProxyManager.getInstance().createProxy(targetClass, baseAspectList);
                // 获取目标实例（从 IOC 容器中获取）
                Object targetInstance = BeanHelper.getInstance().getBean(targetClass);
                // 复制目标实例中的成员变量到代理实例中
                ObjectUtil.copyFields(targetInstance, proxyInstance);
                // 用代理实例覆盖目标实例（放入 IOC 容器中）
                BeanHelper.getInstance().getBeanMap().put(targetClass, proxyInstance);
            }
        } catch (Exception e) {
            logger.error("初始化 AOPHelper 出错！", e);
        }
    }

    public static AOPHelper getInstance() {
        return instance;
    }

    private Map<Class<?>, List<Class<?>>> createAspectMap() throws Exception {
        // 定义 Aspect Map
        Map<Class<?>, List<Class<?>>> aspectMap = new LinkedHashMap<Class<?>, List<Class<?>>>();
        // 添加插件切面
        addPluginAspect(aspectMap);
        // 添加用户界面
        addUserAspect(aspectMap);
        // 添加事务切面
        addTransactionAspect(aspectMap);
        // 返回 Aspect Map
        return aspectMap;
    }

    private void addPluginAspect(Map<Class<?>, List<Class<?>>> aspectMap) throws Exception {
        // 获取插件包名下父类为 PluginAspect 的所有类（插件切面类）
        List<Class<?>> pluginAspectClassList = ClassUtil.getClassListBySuper("com.smart.plugin", PluginAspect.class);
        if (CollectionUtil.isNotEmpty(pluginAspectClassList)) {
            // 遍历所有插件切面类
            for (Class<?> pluginAspectClass : pluginAspectClassList) {
                // 创建插件切面类实例
                PluginAspect pluginAspect = (PluginAspect) pluginAspectClass.newInstance();
                // 初始化插件
                pluginAspect.initPlugin();
                // 将插件切面类及其所对应的目标类列表放入 Aspect Map 中
                aspectMap.put(pluginAspectClass, pluginAspect.getTargetClassList());
            }
        }
    }

    private void addUserAspect(Map<Class<?>, List<Class<?>>> aspectMap) throws Exception {
        // 获取切面类
        List<Class<?>> aspectClassList = ClassHelper.getInstance().getClassListBySuper(BaseAspect.class);
        // 排序切面类
        sortAspectClassList(aspectClassList);
        // 遍历切面类
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
    }

    private void addTransactionAspect(Map<Class<?>, List<Class<?>>> aspectMap) {
        // 使用 TransactionAspect 横切所有 Service 类
        List<Class<?>> serviceClassList = ClassHelper.getInstance().getClassListBySuper(BaseService.class);
        aspectMap.put(TransactionAspect.class, serviceClassList);
    }

    private void sortAspectClassList(List<Class<?>> aspectClassList) {
        // 排序切面类列表
        Collections.sort(aspectClassList, new Comparator<Class<?>>() {
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

    private List<Class<?>> createTargetClassList(Aspect aspect) throws Exception {
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
            targetClassList.addAll(ClassHelper.getInstance().getClassListByPackage(pkg));
        }
        // 返回目标类列表
        return targetClassList;
    }

    private Map<Class<?>, List<Proxy>> createTargetMap(Map<Class<?>, List<Class<?>>> aspectMap) throws Exception {
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
