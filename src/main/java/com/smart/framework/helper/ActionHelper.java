package com.smart.framework.helper;

import com.smart.framework.annotation.Request;
import com.smart.framework.base.BaseAction;
import com.smart.framework.bean.ActionBean;
import com.smart.framework.bean.RequestBean;
import com.smart.framework.util.ArrayUtil;
import com.smart.framework.util.StringUtil;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class ActionHelper {

    private static final Logger logger = Logger.getLogger(ActionHelper.class);

    private static final Map<RequestBean, ActionBean> actionMap = new HashMap<RequestBean, ActionBean>();

    static {
        if (logger.isInfoEnabled()) {
            logger.info("初始化 ActionHelper");
        }

        // 获取并遍历所有 Action 类
        List<Class<?>> actionClassList = ClassHelper.getClassListBySuper(BaseAction.class);
        for (Class<?> actionClass : actionClassList) {
            // 获取并遍历该 Action 类中所有的方法（不包括父类中的方法）
            Method[] actionMethods = actionClass.getDeclaredMethods();
            if (ArrayUtil.isNotEmpty(actionMethods)) {
                for (Method actionMethod : actionMethods) {
                    // 判断当前 Action 方法是否带有 @Request 注解
                    if (actionMethod.isAnnotationPresent(Request.class)) {
                        // 获取 @Requet 注解中的 URL 字符串
                        String[] urlArray = StringUtil.splitString(actionMethod.getAnnotation(Request.class).value(), ":");
                        if (ArrayUtil.isNotEmpty(urlArray)) {
                            // 获取请求方法与请求 URL
                            String requestMethod = urlArray[0];
                            String requestURL = urlArray[1]; // 带有占位符
                            // 将请求 URL 中的占位符转换为 (\d+)（正则表达式）
                            requestURL = StringUtil.replaceAll(requestURL, "\\{\\w+\\}", "(\\\\w+)");
                            // 将 RequestBean 与 ActionBean 放入 Action Map 中
                            actionMap.put(new RequestBean(requestMethod, requestURL), new ActionBean(actionClass, actionMethod));
                        }
                    }
                }
            }
        }
    }

    public static Map<RequestBean, ActionBean> getActionMap() {
        return actionMap;
    }
}
