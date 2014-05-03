package org.smart4j.framework.mvc.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.smart4j.framework.core.FrameworkConstant;
import org.smart4j.framework.ioc.BeanHelper;
import org.smart4j.framework.mvc.ActionHelper;
import org.smart4j.framework.mvc.HandlerMapping;
import org.smart4j.framework.mvc.UploadHelper;
import org.smart4j.framework.mvc.bean.ActionBean;
import org.smart4j.framework.mvc.bean.RequestBean;
import org.smart4j.framework.mvc.fault.AccessException;
import org.smart4j.framework.mvc.fault.PermissionException;
import org.smart4j.framework.mvc.pojo.Params;
import org.smart4j.framework.mvc.pojo.Result;
import org.smart4j.framework.mvc.pojo.View;
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

    @Override
    public ActionBean getAction(String currentRequestMethod, String currentRequestPath) {
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
        // 处理 Action 方法返回值
        handleActionMethodReturn(request, response, actionMethodResult);
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

    private void handleActionMethodReturn(HttpServletRequest request, HttpServletResponse response, Object actionMethodResult) {
        // 判断返回值类型
        if (actionMethodResult != null) {
            if (actionMethodResult instanceof Result) {
                // 分两种情况进行处理
                Result result = (Result) actionMethodResult;
                if (UploadHelper.isMultipart(request)) {
                    // 对于 multipart 类型，说明是文件上传，需要转换为 HTML 格式并写入响应中
                    WebUtil.writeHTML(response, result);
                } else {
                    // 对于其它类型，统一转换为 JSON 格式并写入响应中
                    WebUtil.writeJSON(response, result);
                }
            } else if (actionMethodResult instanceof View) {
                // 转发 或 重定向 到相应的页面中
                View view = (View) actionMethodResult;
                if (view.isRedirect()) {
                    // 获取路径
                    String path = view.getPath();
                    // 重定向请求
                    WebUtil.redirectRequest(path, request, response);
                } else {
                    // 获取路径
                    String path = FrameworkConstant.JSP_PATH + view.getPath();
                    // 初始化请求属性
                    Map<String, Object> data = view.getData();
                    if (MapUtil.isNotEmpty(data)) {
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            request.setAttribute(entry.getKey(), entry.getValue());
                        }
                    }
                    // 转发请求
                    WebUtil.forwardRequest(path, request, response);
                }
            }
        }
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
