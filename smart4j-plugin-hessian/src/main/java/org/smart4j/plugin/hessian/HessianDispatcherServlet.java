package org.smart4j.plugin.hessian;

import com.caucho.hessian.server.HessianServlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import org.smart4j.framework.core.ClassHelper;
import org.smart4j.framework.ioc.BeanHelper;
import org.smart4j.framework.ioc.IocHelper;
import org.smart4j.framework.util.CollectionUtil;
import org.smart4j.framework.util.WebUtil;

@WebServlet(urlPatterns = HessianConstant.URL_PREFIX + "/*", loadOnStartup = 0)
public class HessianDispatcherServlet extends HessianServlet {

    // 定义一个 Hessian Servlet Map，用于管理 Hessian URL 与 Hessian Servlet 之间的映射关系
    private final Map<String, HessianServlet> hessianServletMap = new HashMap<String, HessianServlet>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // 获取所有标注了 @Hessian 注解的类（接口）
        List<Class<?>> hessianInterfaceList = ClassHelper.getClassListByAnnotation(Hessian.class);
        if (CollectionUtil.isNotEmpty(hessianInterfaceList)) {
            // 遍历所有 Hessian 接口
            for (Class<?> hessianInterface : hessianInterfaceList) {
                // 获取 Hessian URL
                String url = hessianInterface.getAnnotation(Hessian.class).value();
                // 获取 Hessian 接口的实现类
                Class<?> implClass = IocHelper.findImplementClass(hessianInterface);
                // 获取实现类实例
                Object implInstance = BeanHelper.getBean(implClass);
                // 创建 Hessian Servlet
                HessianServlet hessianServlet = new HessianServlet();
                hessianServlet.setHomeAPI(hessianInterface); // 设置接口
                hessianServlet.setHome(implInstance); // 设置实现类实例
                hessianServlet.init(config); // 初始化 Servlet
                // 将 Hessian URL 与 Hessian Servlet 放入 Hessian Servlet Map 中
                hessianServletMap.put(HessianConstant.URL_PREFIX + url, hessianServlet);
            }
        }
    }

    @Override
    public void service(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        // 获取请求 URL
        HttpServletRequest req = (HttpServletRequest) request;
        String url = WebUtil.getRequestPath(req);
        // 从 Hessian Servlet Map 中获取 Hessian Servlet
        HessianServlet hessianServlet = hessianServletMap.get(url);
        if (hessianServlet != null) {
            // 执行 Servlet
            hessianServlet.service(request, response);
        }
    }
}
