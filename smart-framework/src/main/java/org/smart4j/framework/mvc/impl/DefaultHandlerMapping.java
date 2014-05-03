package org.smart4j.framework.mvc.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.smart4j.framework.core.FrameworkConstant;
import org.smart4j.framework.core.InstanceFactory;
import org.smart4j.framework.ioc.BeanHelper;
import org.smart4j.framework.mvc.ActionHelper;
import org.smart4j.framework.mvc.HandlerMapping;
import org.smart4j.framework.mvc.UploadHelper;
import org.smart4j.framework.mvc.ViewResolver;
import org.smart4j.framework.mvc.bean.ActionBean;
import org.smart4j.framework.mvc.bean.RequestBean;
import org.smart4j.framework.mvc.fault.AccessException;
import org.smart4j.framework.mvc.fault.PermissionException;
import org.smart4j.framework.mvc.pojo.Params;
import org.smart4j.framework.util.CastUtil;
import org.smart4j.framework.util.MapUtil;
import org.smart4j.framework.util.WebUtil;

/**
 * 默认处理器映射
 *
 * @author huangyong
 * @since 2.3
 */
public class DefaultHandlerMapping implements HandlerMapping {

    // 用于缓存 ActionBean 实例
    private static final Map<String, ActionBean> cache = new ConcurrentHashMap<String, ActionBean>();

    @Override
    public ActionBean getAction(String currentRequestMethod, String currentRequestPath) {
        // 若缓存中存在对应的实例，则返回该实例
        String cacheKey = currentRequestMethod + ":" + currentRequestPath;
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }
        // 定义一个 ActionBean
        ActionBean actionBean = null;
        // 获取并遍历 Action 映射
        Map<RequestBean, ActionBean> actionMap = ActionHelper.getActionMap();
        for (Map.Entry<RequestBean, ActionBean> actionEntry : actionMap.entrySet()) {
            // 从 RequestBean 中获取 Request 相关属性
            RequestBean requestBean = actionEntry.getKey();
            String requestMethod = requestBean.getRequestMethod();
            String requestPath = requestBean.getRequestPath(); // 正则表达式
            // 获取请求路径匹配器（使用正则表达式匹配请求路径并从中获取相应的请求参数）
            Matcher requestPathMatcher = Pattern.compile(requestPath).matcher(currentRequestPath);
            // 判断请求方法与请求路径是否同时匹配
            if (requestMethod.equalsIgnoreCase(currentRequestMethod) && requestPathMatcher.matches()) {
                // 获取 ActionBean 及其相关属性
                actionBean = actionEntry.getValue();
                // 设置请求路径匹配器
                if (actionBean != null) {
                    actionBean.setRequestPathMatcher(requestPathMatcher);
                }
                // 若成功匹配，则终止循环
                break;
            }
        }
        // 若该实例不为空，则将其放入缓存
        if (actionBean != null) {
            cache.put(cacheKey, actionBean);
        }
        // 返回该 ActionBean
        return actionBean;
    }

    @Override
    public void invokeActionMethod(HttpServletRequest request, HttpServletResponse response, ActionBean actionBean) throws Exception {
        // 获取 Action 相关信息
        Class<?> actionClass = actionBean.getActionClass();
        Method actionMethod = actionBean.getActionMethod();
        // 从 BeanHelper 中创建 Action 实例
        Object actionInstance = BeanHelper.getBean(actionClass);
        // 获取 Action 方法参数
        List<Object> paramList = createParamList(request, actionBean);
        Class<?>[] paramTypes = actionMethod.getParameterTypes();
        if (paramTypes.length != paramList.size()) {
            throw new RuntimeException("由于参数不匹配，无法调用 Action 方法！");
        }
        // 调用 Action 方法
        actionMethod.setAccessible(true); // 取消类型安全检测（可提高反射性能）
        Object actionMethodResult = actionMethod.invoke(actionInstance, paramList.toArray());
        // 获取视图解析器
        ViewResolver viewResolver = InstanceFactory.createViewResolver();
        // 解析视图
        viewResolver.resolveView(request, response, actionMethodResult);
    }

    private List<Object> createParamList(HttpServletRequest request, ActionBean actionBean) throws Exception {
        // 定义参数列表
        List<Object> paramList = new ArrayList<Object>();
        // 获取 Action 方法参数类型
        Class<?>[] actionParamTypes = actionBean.getActionMethod().getParameterTypes();
        // 添加路径参数列表（请求路径中的带占位符参数）
        paramList.addAll(createPathParamList(actionBean.getRequestPathMatcher(), actionParamTypes));
        // 分两种情况进行处理
        if (UploadHelper.isMultipart(request)) {
            // 添加 Multipart 请求参数列表
            paramList.addAll(UploadHelper.createMultipartParamList(request));
        } else {
            // 添加普通请求参数列表（包括 Query String 与 Form Data）
            Map<String, Object> requestParamMap = WebUtil.getRequestParamMap(request);
            if (MapUtil.isNotEmpty(requestParamMap)) {
                paramList.add(new Params(requestParamMap));
            }
        }
        // 返回参数列表
        return paramList;
    }

    private List<Object> createPathParamList(Matcher requestPathMatcher, Class<?>[] actionParamTypes) {
        // 定义参数列表
        List<Object> paramList = new ArrayList<Object>();
        // 遍历正则表达式中所匹配的组
        for (int i = 1; i <= requestPathMatcher.groupCount(); i++) {
            // 获取请求参数
            String param = requestPathMatcher.group(i);
            // 获取参数类型（支持四种类型：int/Integer、long/Long、double/Double、String）
            Class<?> paramType = actionParamTypes[i - 1];
            if (paramType.equals(int.class) || paramType.equals(Integer.class)) {
                paramList.add(CastUtil.castInt(param));
            } else if (paramType.equals(long.class) || paramType.equals(Long.class)) {
                paramList.add(CastUtil.castLong(param));
            } else if (paramType.equals(double.class) || paramType.equals(Double.class)) {
                paramList.add(CastUtil.castDouble(param));
            } else if (paramType.equals(String.class)) {
                paramList.add(param);
            }
        }
        // 返回参数列表
        return paramList;
    }

    @Override
    public void handleActionException(HttpServletRequest request, HttpServletResponse response, Exception e) {
        // 判断异常原因
        Throwable cause = e.getCause();
        if (cause instanceof AccessException) {
            // 分两种情况进行处理
            if (WebUtil.isAJAX(request)) {
                // 跳转到 403 页面
                WebUtil.sendError(HttpServletResponse.SC_FORBIDDEN, "", response);
            } else {
                // 重定向到首页
                WebUtil.redirectRequest(FrameworkConstant.HOME_PAGE, request, response);
            }
        } else if (cause instanceof PermissionException) {
            // 跳转到 403 页面
            WebUtil.sendError(HttpServletResponse.SC_FORBIDDEN, "", response);
        } else {
            // 跳转到 500 页面
            WebUtil.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, cause.getMessage(), response);
        }
    }
}
