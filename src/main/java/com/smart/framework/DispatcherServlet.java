package com.smart.framework;

import com.smart.framework.bean.ActionBean;
import com.smart.framework.bean.Page;
import com.smart.framework.bean.RequestBean;
import com.smart.framework.bean.Result;
import com.smart.framework.helper.ActionHelper;
import com.smart.framework.helper.BeanHelper;
import com.smart.framework.helper.ConfigHelper;
import com.smart.framework.util.CastUtil;
import com.smart.framework.util.MapUtil;
import com.smart.framework.util.StringUtil;
import com.smart.framework.util.WebUtil;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

@WebServlet("/*")
public class DispatcherServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(DispatcherServlet.class);

    // 获取相关配置项
    private final String homePage = ConfigHelper.getInstance().getStringProperty("app.home_page");
    private final String jspPath = ConfigHelper.getInstance().getStringProperty("app.jsp_path");

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取当前请求相关数据
        String currentRequestMethod = request.getMethod();
        String currentRequestURL = request.getPathInfo();
        if (logger.isDebugEnabled()) {
            logger.debug(currentRequestMethod + ":" + currentRequestURL);
        }
        // 将“/”请求重定向到首页
        if (currentRequestURL.equals("/")) {
            WebUtil.redirectRequest(homePage, request, response);
            return;
        }
        // 去掉请求最后的“/”
        if (currentRequestURL.endsWith("/")) {
            currentRequestURL = currentRequestURL.substring(0, currentRequestURL.length() - 1);
        }
        // 定义一个 JSP 映射标志（默认为映射失败）
        boolean jspMapped = false;
        // 初始化 DataContext
        DataContext.init(request, response);
        // 获取请求参数映射（包括：Query String 与 Form Data）
        Map<String, String> requestParamMap = WebUtil.getRequestParamMap(request);
        try {
            // 获取并遍历 Action 映射
            Map<RequestBean, ActionBean> actionMap = ActionHelper.getInstance().getActionMap();
            for (Map.Entry<RequestBean, ActionBean> actionEntry : actionMap.entrySet()) {
                // 从 RequestBean 中获取 Request 相关属性
                RequestBean requestBean = actionEntry.getKey();
                String requestURL = requestBean.getRequestURL(); // 正则表达式
                String requestMethod = requestBean.getRequestMethod();
                // 获取正则表达式匹配器（用于匹配请求 URL 并从中获取相应的请求参数）
                Matcher matcher = Pattern.compile(requestURL).matcher(currentRequestURL);
                // 判断请求方法与请求 URL 是否同时匹配
                if (requestMethod.equalsIgnoreCase(currentRequestMethod) && matcher.matches()) {
                    // 获取 Action 对象
                    ActionBean actionBean = actionEntry.getValue();
                    // 创建 Action 方法参数列表
                    List<Object> paramList = createParamList(requestParamMap, matcher);
                    // 处理 Action 方法
                    handleActionMethod(request, response, actionBean, paramList);
                    // 设置为映射成功
                    jspMapped = true;
                    // 若成功匹配，则终止循环
                    break;
                }
            }
        } finally {
            // 销毁 DataContext
            DataContext.destroy();
        }
        // 若 JSP 映射失败，则根据默认路由规则转发请求
        if (!jspMapped && StringUtil.isNotEmpty(jspPath)) {
            // 获取路径（默认路由规则：/{1}/{2} => /xxx/{1}_{2}.jsp）
            String path = jspPath + currentRequestURL.substring(1).replace("/", "_") + ".jsp";
            // 转发请求
            request.setAttribute("path", path);
            WebUtil.forwardRequest(path, request, response);
        }
    }

    private List<Object> createParamList(Map<String, String> requestParamMap, Matcher matcher) {
        List<Object> paramList = new ArrayList<Object>();
        // 遍历正则表达式中所匹配的组
        for (int i = 1; i <= matcher.groupCount(); i++) {
            String param = matcher.group(i);
            // 若为数字，则需要强制转型，并放入参数列表中
            // 注意：必须转型为低级别类型（低级别类型可安全转换为高级别类型使用）
            if (StringUtil.isNumber(param)) {
                if (StringUtil.isDigits(param)) {
                    paramList.add(CastUtil.castInt(param));
                } else {
                    paramList.add(CastUtil.castFloat(param));
                }
            } else {
                paramList.add(param);
            }
        }
        // 向参数列表中添加请求参数映射
        if (MapUtil.isNotEmpty(requestParamMap)) {
            paramList.add(requestParamMap);
        }
        return paramList;
    }

    private void handleActionMethod(HttpServletRequest request, HttpServletResponse response, ActionBean actionBean, List<Object> paramList) {
        // 从 ActionBean 中获取 Action 相关属性
        Class<?> actionClass = actionBean.getActionClass();
        Method actionMethod = actionBean.getActionMethod();
        // 从 BeanHelper 中创建 Action 实例
        Object actionInstance = BeanHelper.getInstance().getBean(actionClass);
        // 调用 Action 方法
        Object actionMethodResult;
        try {
            actionMethod.setAccessible(true); // 取消类型安全检测（可提高反射性能）
            actionMethodResult = actionMethod.invoke(actionInstance, paramList.toArray());
        } catch (Exception e) {
            // 处理 Action 方法异常
            handleActionMethodException(request, response, e);
            // 直接返回
            return;
        }
        // 处理 Action 方法返回值
        handleActionMethodReturn(request, response, actionMethodResult);
    }

    private void handleActionMethodException(HttpServletRequest request, HttpServletResponse response, Exception e) {
        if (e.getCause() instanceof AuthException) {
            // 若为认证异常，则分两种情况进行处理
            if (WebUtil.isAJAX(request)) {
                // 若为 AJAX 请求，则发送 403 错误
                WebUtil.sendError(403, response);
            } else {
                // 否则重定向到首页
                WebUtil.redirectRequest("/", request, response);
            }
        } else {
            // 若为其他异常，则记录错误日志
            logger.error("调用 Action 方法出错！", e);
            throw new RuntimeException(e); // 这里需要向上抛出异常，否则无法定位到错误页面
        }
    }

    private void handleActionMethodReturn(HttpServletRequest request, HttpServletResponse response, Object actionMethodResult) {
        // 判断返回值类型
        if (actionMethodResult != null) {
            if (actionMethodResult instanceof Result) {
                // 若为 Result 类型，则转换为 JSON 格式并写入响应中
                Result result = (Result) actionMethodResult;
                WebUtil.writeJSON(response, result);
            } else if (actionMethodResult instanceof Page) {
                // 若为 Page 类型，则 转发 或 重定向 到相应的页面中
                Page page = (Page) actionMethodResult;
                if (page.isRedirect()) {
                    // 获取路径
                    String path = page.getPath();
                    // 重定向请求
                    WebUtil.redirectRequest(path, request, response);
                } else {
                    // 获取路径
                    String path = jspPath + page.getPath();
                    // 初始化请求属性
                    Map<String, Object> data = page.getData();
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
}
