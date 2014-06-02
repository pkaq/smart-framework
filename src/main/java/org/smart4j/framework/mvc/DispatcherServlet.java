package org.smart4j.framework.mvc;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.framework.FrameworkConstant;
import org.smart4j.framework.InstanceFactory;
import org.smart4j.framework.util.WebUtil;

/**
 * 前端控制器
 *
 * @author huangyong
 * @since 1.0
 */
@WebServlet(urlPatterns = "/*", loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(DispatcherServlet.class);

    private HandlerMapping handlerMapping = InstanceFactory.getHandlerMapping();
    private HandlerInvoker handlerInvoker = InstanceFactory.getHandlerInvoker();
    private HandlerExceptionResolver handlerExceptionResolver = InstanceFactory.getHandlerExceptionResolver();

    @Override
    public void init(ServletConfig config) throws ServletException {
        // 初始化相关配置
        ServletContext servletContext = config.getServletContext();
        UploadHelper.init(servletContext);
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置请求编码方式
        request.setCharacterEncoding(FrameworkConstant.UTF_8);
        // 获取当前请求相关数据
        String currentRequestMethod = request.getMethod();
        String currentRequestPath = WebUtil.getRequestPath(request);
        logger.debug("[Smart] {}:{}", currentRequestMethod, currentRequestPath);
        // 将“/”请求重定向到首页
        if (currentRequestPath.equals("/")) {
            WebUtil.redirectRequest(FrameworkConstant.HOME_PAGE, request, response);
            return;
        }
        // 去掉当前请求路径末尾的“/”
        if (currentRequestPath.endsWith("/")) {
            currentRequestPath = currentRequestPath.substring(0, currentRequestPath.length() - 1);
        }
        // 获取 Handler
        Handler handler = handlerMapping.getHandler(currentRequestMethod, currentRequestPath);
        // 若未找到 Action，则跳转到 404 页面
        if (handler == null) {
            WebUtil.sendError(HttpServletResponse.SC_NOT_FOUND, "", response);
            return;
        }
        // 初始化 DataContext
        DataContext.init(request, response);
        try {
            // 调用 Handler
            handlerInvoker.invokeHandler(request, response, handler);
        } catch (Exception e) {
            // 处理 Action 异常
            handlerExceptionResolver.resolveHandlerException(request, response, e);
        } finally {
            // 销毁 DataContext
            DataContext.destroy();
        }
    }
}
