package org.smart4j.plugin.xmlrpc;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.webserver.XmlRpcServlet;
import org.smart4j.framework.core.ClassHelper;
import org.smart4j.framework.util.CollectionUtil;

@WebServlet(urlPatterns = XmlRpcConstant.SERVLET_URL, loadOnStartup = 0)
public class SmartXmlRpcServlet extends XmlRpcServlet {

    @Override
    protected XmlRpcHandlerMapping newXmlRpcHandlerMapping() throws XmlRpcException {
        try {
            return newPropertyHandlerMapping(null);
        } catch (IOException e) {
            throw new XmlRpcException("", e);
        }
    }

    @Override
    protected PropertyHandlerMapping newPropertyHandlerMapping(URL url) throws IOException, XmlRpcException {
        PropertyHandlerMapping propertyHandlerMapping = new PropertyHandlerMapping();
        List<Class<?>> xmlrpcClassList = ClassHelper.getClassListByAnnotation(XmlRpc.class);
        if (CollectionUtil.isNotEmpty(xmlrpcClassList)) {
            for (Class<?> xmlrpcClass : xmlrpcClassList) {
                propertyHandlerMapping.addHandler(xmlrpcClass.getSimpleName(), xmlrpcClass);
            }
        }
        return propertyHandlerMapping;
    }
}
