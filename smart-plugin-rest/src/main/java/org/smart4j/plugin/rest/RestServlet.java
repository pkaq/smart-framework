package org.smart4j.plugin.rest;

import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.smart4j.framework.core.ClassHelper;
import org.smart4j.framework.util.CollectionUtil;
import org.smart4j.framework.util.StringUtil;

@WebServlet(urlPatterns = RestConstant.SERVLET_URL, loadOnStartup = 0)
public class RestServlet extends CXFNonSpringServlet {

    @Override
    protected void loadBus(ServletConfig sc) {
        // 初始化 CXF 总线
        super.loadBus(sc);
        Bus bus = getBus();
        BusFactory.setDefaultBus(bus);
        // 发布 REST 服务
        publishRESTService();
    }

    private void publishRESTService() {
        // 遍历所有标注了 Rest 注解的类
        List<Class<?>> classList = ClassHelper.getClassListByAnnotation(Rest.class);
        if (CollectionUtil.isNotEmpty(classList)) {
            for (Class<?> cls : classList) {
                // 获取 REST 地址
                String address = getAddress(cls);
                // 发布 REST 服务
                RestHelper.publishService(address, cls);
            }
        }
    }

    private String getAddress(Class<?> class_) {
        String address;
        // 若 Rest 注解的 value 属性不为空，则获取当前值，否则获取类名
        String value = class_.getAnnotation(Rest.class).value();
        if (StringUtil.isNotEmpty(value)) {
            address = value;
        } else {
            address = class_.getSimpleName();
        }
        // 确保最前面只有一个 /
        if (!address.startsWith("/")) {
            address = "/" + address;
        }
        address = address.replaceAll("\\/+", "/");
        return address;
    }
}
