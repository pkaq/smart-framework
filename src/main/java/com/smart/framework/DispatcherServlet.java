package com.smart.framework;

import com.smart.framework.bean.ActionBean;
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
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

@WebServlet(urlPatterns = "/*", loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(DispatcherServlet.class);

    private static final ThreadLocal<HttpSession> sessionContainer = new ThreadLocal<HttpSession>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        // 初始化 Helper 类
        InitHelper.init();

        // 添加 Servlet 映射
        addServletMapping(config.getServletContext());
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 放入 Session
        HttpSession session = request.getSession(false);
        if (session != null) {
            sessionContainer.set(session);
        }

        // 获取当前请求相关数据
        String currentRequestMethod = request.getMethod();
        String currentRequestURL = request.getPathInfo();

        // 获取请求参数映射（包括：Query String 与 Form Data）
        Map<String, String> requestParamMap = WebUtil.getRequestParamMap(request);

        // 获取并遍历 Action 映射
        Map<RequestBean, ActionBean> actionMap = ActionHelper.getActionMap();
        for (Map.Entry<RequestBean, ActionBean> actionEntry : actionMap.entrySet()) {
            // 从 RequestBean 中获取 Request 相关属性
            RequestBean reqestBean = actionEntry.getKey();
            String requestURL = reqestBean.getRequestURL(); // 正则表达式
            String requestMethod = reqestBean.getRequestMethod();
            // 获取正则表达式匹配器（用于匹配请求 URL 并从中获取相应的请求参数）
            Matcher matcher = Pattern.compile(requestURL).matcher(currentRequestURL);
            // 判断请求方法与请求 URL 是否同时匹配
            if (requestMethod.equalsIgnoreCase(currentRequestMethod) && matcher.matches()) {
                // 初始化 Action 对象
                ActionBean actionBean = actionEntry.getValue();
                // 初始化 Action 方法参数列表
                List<Object> paramList = new ArrayList<Object>();
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
                // 从 ActionBean 中获取 Action 相关属性
                Class<?> actionClass = actionBean.getActionClass();
                Method actionMethod = actionBean.getActionMethod();
                try {
                    // 创建 Action 实例
                    Object actionInstance = BeanHelper.getBean(actionClass);
                    // 调用 Action 方法（传入请求参数）
                    actionMethod.setAccessible(true); // 取消类型安全检测（可提高反射性能）
                    Object actionMethodResult = actionMethod.invoke(actionInstance, paramList.toArray());
                    if (actionMethodResult instanceof Result) {
                        // 获取 Action 方法返回值
                        Result result = (Result) actionMethodResult;
                        // 将返回值转为 JSON 格式并写入 Response 中
                        WebUtil.writeJSON(response, result);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    throw new RuntimeException(e.getMessage(), e);
                }
                // 若成功匹配，则终止循环
                break;
            }
        }
    }

    public static HttpSession getSession() {
        HttpSession session = sessionContainer.get();
        if (session == null) {
            throw new RuntimeException("Can not get session!");
        }
        return session;
    }

    private void addServletMapping(ServletContext context) {
        // 用 DefaultServlet 映射所有静态资源
        ServletRegistration defaultServletRegistration = context.getServletRegistration("default");
        defaultServletRegistration.addMapping("/favicon.ico", "/www/*");

        // 用 UploadServlet 映射 /upload.do 请求
        ServletRegistration uploadServletRegistration = context.getServletRegistration("upload");
        uploadServletRegistration.addMapping("/upload.do");
    }
}
