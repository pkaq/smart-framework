package com.smart.framework;

import com.smart.framework.bean.ActionBean;
import com.smart.framework.bean.Page;
import com.smart.framework.bean.RequestBean;
import com.smart.framework.bean.Result;
import com.smart.framework.helper.ActionHelper;
import com.smart.framework.helper.BeanHelper;
import com.smart.framework.helper.InitHelper;
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
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

@WebServlet(urlPatterns = "/*", loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(DispatcherServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        // 初始化 Helper 类
        InitHelper.init();

        // 添加 Servlet 映射
        addServletMapping(config.getServletContext());
    }

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
            response.sendRedirect("/static/page/index.html");
            return;
        }

        // 定义一个映射标志（默认为映射失败）
        boolean mapped = false;

        try {
            // 初始化 DataContext
            DataContext.init(request, response);

            // 获取请求参数映射（包括：Query String 与 Form Data）
            Map<String, String> requestParamMap = WebUtil.getRequestParamMap(request);

            // 获取并遍历 Action 映射
            Map<RequestBean, ActionBean> actionMap = ActionHelper.getActionMap();
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
                    handleActionMethod(actionBean, paramList, request, response);
                    // 设置为映射成功
                    mapped = true;
                    // 若成功匹配，则终止循环
                    break;
                }
            }
        } finally {
            // 销毁 DataContext
            DataContext.destroy();
        }

        // 若映射失败，则路由请求
        if (!mapped) {
            routeRequest(currentRequestURL, request, response);
        }
    }

    private void addServletMapping(ServletContext context) {
        // 用 DefaultServlet 映射所有静态资源
        ServletRegistration defaultServletRegistration = context.getServletRegistration("default");
        defaultServletRegistration.addMapping("/favicon.ico", "/static/*", "/index.html");

        // 用 JspServlet 映射所有 JSP 请求
        ServletRegistration jspServletRegistration = context.getServletRegistration("jsp");
        jspServletRegistration.addMapping("/dynamic/jsp/*");

        // 用 UploadServlet 映射 /upload.do 请求
        ServletRegistration uploadServletRegistration = context.getServletRegistration("upload");
        uploadServletRegistration.addMapping("/upload.do");
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

    private void handleActionMethod(ActionBean actionBean, List<Object> paramList, HttpServletRequest request, HttpServletResponse response) {
        // 从 ActionBean 中获取 Action 相关属性
        Class<?> actionClass = actionBean.getActionClass();
        Method actionMethod = actionBean.getActionMethod();
        // 从 BeanHelper 中创建 Action 实例
        Object actionInstance = BeanHelper.getBean(actionClass);
        // 调用 Action 方法
        Object actionMethodResult = invokeActionMethod(actionInstance, actionMethod, paramList);
        // 判断返回值类型
        if (actionMethodResult != null) {
            if (actionMethodResult instanceof Result) {
                // 若为 Result 类型，则转换为 JSON 格式并写入 Response 中
                Result result = (Result) actionMethodResult;
                WebUtil.writeJSON(response, result);
            } else if (actionMethodResult instanceof Page) {
                // 若为 Page 类型，则 转发 或 重定向 到相应的页面中
                Page page = (Page) actionMethodResult;
                if (page.isRedirect()) {
                    // 重定向请求
                    redirectRequest(page, response);
                } else {
                    // 转发请求
                    forwordRequest(page, request, response);
                }
            }
        }
    }

    private Object invokeActionMethod(Object actionInstance, Method actionMethod, List<Object> paramList) {
        Object actionMethodResult;
        try {
            actionMethod.setAccessible(true); // 取消类型安全检测（可提高反射性能）
            actionMethodResult = actionMethod.invoke(actionInstance, paramList.toArray());
        } catch (Exception e) {
            logger.error("调用 Action 方法出错！", e);
            throw new RuntimeException(e);
        }
        return actionMethodResult;
    }

    private void redirectRequest(Page page, HttpServletResponse response) {
        try {
            // 获取路径
            String path = page.getPath();
            // 重定向请求
            response.sendRedirect(path);
        } catch (Exception e) {
            logger.error("页面重定向出错！", e);
            throw new RuntimeException(e);
        }
    }

    private void forwordRequest(Page page, HttpServletRequest request, HttpServletResponse response) {
        try {
            // 获取路径
            String path = "/dynamic/jsp/" + page.getPath();
            // 初始化 Request 属性
            Map<String, Object> data = page.getData();
            if (MapUtil.isNotEmpty(data)) {
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    request.setAttribute(entry.getKey(), entry.getValue());
                }
            }
            // 转发请求
            request.getRequestDispatcher(path).forward(request, response);
        } catch (Exception e) {
            logger.error("页面转发出错！", e);
            throw new RuntimeException(e);
        }
    }

    private void routeRequest(String url, HttpServletRequest request, HttpServletResponse response) {
        try {
            // 获取路径
            String path = "/dynamic/jsp/" + url.substring(1).replace("/", "_") + ".jsp";
            // 转发请求
            request.getRequestDispatcher(path).forward(request, response);
        } catch (Exception e) {
            logger.error("页面转发出错！", e);
            throw new RuntimeException(e);
        }
    }
}
