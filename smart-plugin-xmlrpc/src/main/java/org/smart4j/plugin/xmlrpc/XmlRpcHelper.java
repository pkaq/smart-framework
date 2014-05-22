package org.smart4j.plugin.xmlrpc;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.smart4j.framework.core.ConfigHelper;
import org.smart4j.framework.util.StringUtil;

/**
 * 执行 XML-RPC 方法
 */
public class XmlRpcHelper {

    private static XmlRpcClientConfigImpl config;

    static {
        String xmlrpcURL = ConfigHelper.getString("smart.plugin.xmlrpc.url");
        boolean xmlrpcExtensionsEnabled = ConfigHelper.getBoolean("smart.plugin.xmlrpc.extensions_enabled", true);
        int xmlrpcConnectionTimeout = ConfigHelper.getInt("smart.plugin.xmlrpc.connection_timeout", 60 * 1000);
        int xmlrpcReplyTimeout = ConfigHelper.getInt("smart.plugin.xmlrpc.reply_timeout", 60 * 1000);

        if (StringUtil.isEmpty(xmlrpcURL)) {
            throw new RuntimeException("错误：请在 smart.properties 中设置 xmlrpc.url 参数！");
        }

        URL serverURL;
        try {
            serverURL = new URL(xmlrpcURL);
        } catch (MalformedURLException e) {
            throw new RuntimeException("错误：无法连接到 XML-RPC 地址！xmlrpc.url: " + xmlrpcURL, e);
        }

        config = new XmlRpcClientConfigImpl();
        config.setServerURL(serverURL);
        config.setEnabledForExtensions(xmlrpcExtensionsEnabled);
        config.setConnectionTimeout(xmlrpcConnectionTimeout);
        config.setReplyTimeout(xmlrpcReplyTimeout);
    }

    /**
     * 执行方法并返回基本数据类型<br/>
     * 包括：Integer、Integer、String、Double、java.util.Date、byte[]
     *
     * @param method 方法名
     * @param params 相关参数
     * @return 基本数据类型对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T executeReturnBasic(String method, Object... params) {
        T result;
        try {
            result = (T) createXmlRpcClient(method).execute(method, params);
        } catch (XmlRpcException e) {
            throw new RuntimeException("错误：执行客户端方法失败！", e);
        }
        return result;
    }

    /**
     * 执行方法并返回 Array 数据类型
     *
     * @param method 方法名
     * @param params 相关参数
     * @return Array 对象
     */
    public static Object[] executeReturnArray(String method, Object... params) {
        Object[] result;
        try {
            result = (Object[]) createXmlRpcClient(method).execute(method, params);
        } catch (XmlRpcException e) {
            throw new RuntimeException("错误：执行客户端方法失败！", e);
        }
        return result;
    }

    /**
     * 执行方法并返回 Map 数据类型
     *
     * @param method 方法名
     * @param params 相关参数
     * @return Map 对象
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> executeReturnMap(String method, Object... params) {
        Map<K, V> result;
        try {
            result = (Map<K, V>) createXmlRpcClient(method).execute(method, params);
        } catch (XmlRpcException e) {
            throw new RuntimeException("错误：执行客户端方法失败！", e);
        }
        return result;
    }

    private static XmlRpcClient createXmlRpcClient(String method) {
        if (StringUtil.isEmpty(method)) {
            throw new IllegalArgumentException("错误：参数 method 为空！");
        }
        XmlRpcClient client = new XmlRpcClient();
        client.setTransportFactory(new XmlRpcCommonsTransportFactory(client));
        client.setConfig(config);
        return client;
    }
}
