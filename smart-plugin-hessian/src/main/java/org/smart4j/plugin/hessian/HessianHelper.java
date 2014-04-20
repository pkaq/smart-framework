package org.smart4j.plugin.hessian;

import com.caucho.hessian.client.HessianProxyFactory;
import java.net.MalformedURLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HessianHelper {

    private static final Logger logger = LoggerFactory.getLogger(HessianHelper.class);

    @SuppressWarnings("unchecked")
    public static <T> T createClient(String hessianURL, Class<T> interfaceClass) {
        T client = null;
        try {
            HessianProxyFactory factory = new HessianProxyFactory();
            client = (T) factory.create(interfaceClass, hessianURL);
        } catch (MalformedURLException e) {
            logger.error("创建 Hessian 客户端出错！", e);
        }
        return client;
    }
}
