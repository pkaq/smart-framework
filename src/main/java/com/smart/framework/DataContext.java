package com.smart.framework;

import com.smart.framework.util.CastUtil;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DataContext {

    private static final ThreadLocal<DataContext> dataContextContainer = new ThreadLocal<DataContext>();

    private HttpServletRequest request;
    private HttpServletResponse response;

    // 初始化
    public static void init(HttpServletRequest request, HttpServletResponse response) {
        DataContext dataContext = new DataContext();
        dataContext.request = request;
        dataContext.response = response;
        dataContextContainer.set(dataContext);
    }

    // 销毁
    public static void destroy() {
        dataContextContainer.remove();
    }

    // 获取 Request
    private static HttpServletRequest getRequest() {
        return dataContextContainer.get().request;
    }

    // 获取 Response
    private static HttpServletResponse getResponse() {
        return dataContextContainer.get().response;
    }

    // 获取 Session
    private static HttpSession getSession() {
        return getRequest().getSession();
    }

    // 获取 Servlet Context
    private static ServletContext getServletContext() {
        return getRequest().getServletContext();
    }

    // 封装 Request 相关操作
    public static class Request {

        // 将数据放入 Request Attribute 中
        public static void put(String key, Object value) {
            getRequest().setAttribute(key, value);
        }

        // 从 Request 中获取数据
        @SuppressWarnings("unchecked")
        public static <T> T get(String key) {
            return (T) getRequest().getAttribute(key);
        }

        // 移除 Request 中的数据
        public static void remove(String key) {
            getRequest().removeAttribute(key);
        }

        // 从 Request 中获取所有数据
        public static Map<String, Object> getAll() {
            Map<String, Object> map = new HashMap<String, Object>();
            Enumeration<String> names = getRequest().getAttributeNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                map.put(name, getRequest().getAttribute(name));
            }
            return map;
        }
    }

    // 封装 Response 相关操作
    public static class Response {

        // 将数据放入 Response Header 中
        public static void put(String key, Object value) {
            getResponse().setHeader(key, CastUtil.castString(value));
        }

        // 从 Response Header 中获取数据
        @SuppressWarnings("unchecked")
        public static <T> T get(String key) {
            return (T) getResponse().getHeader(key);
        }

        // 从 Response Header 中获取所有数据
        public static Map<String, Object> getAll() {
            Map<String, Object> map = new HashMap<String, Object>();
            for (String name : getResponse().getHeaderNames()) {
                map.put(name, getResponse().getHeader(name));
            }
            return map;
        }
    }

    // 封装 Session 相关操作
    public static class Session {

        // 将数据放入 Session Attribute 中
        public static void put(String key, Object value) {
            getSession().setAttribute(key, value);
        }

        // 从 Session Attribute 中获取数据
        @SuppressWarnings("unchecked")
        public static <T> T get(String key) {
            return (T) getSession().getAttribute(key);
        }

        // 移除 Session Attribute 中的数据
        public static void remove(String key) {
            getSession().removeAttribute(key);
        }

        // 从 Session Attribute 中获取所有数据
        public static Map<String, Object> getAll() {
            Map<String, Object> map = new HashMap<String, Object>();
            Enumeration<String> names = getSession().getAttributeNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                map.put(name, getSession().getAttribute(name));
            }
            return map;
        }

        // 移除 Session Attribute 中所有的数据
        public static void removeAll() {
            getSession().invalidate();
        }
    }

    // 封装 Servlet Context 相关操作
    public static class Context {

        // 将数据放入 Servlet Context 中
        public static void put(String key, Object value) {
            getServletContext().setAttribute(key, value);
        }

        // 从 Servlet Context 中获取数据
        @SuppressWarnings("unchecked")
        public static <T> T get(String key) {
            return (T) getServletContext().getAttribute(key);
        }

        // 移除 Servlet Context 中的数据
        public static void remove(String key) {
            getServletContext().removeAttribute(key);
        }

        // 从 Servlet Context 中获取所有数据
        public static Map<String, Object> getAll() {
            Map<String, Object> map = new HashMap<String, Object>();
            Enumeration<String> names = getServletContext().getAttributeNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                map.put(name, getServletContext().getAttribute(name));
            }
            return map;
        }
    }
}
