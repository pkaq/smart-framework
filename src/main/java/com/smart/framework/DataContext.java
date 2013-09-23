package com.smart.framework;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;

public class DataContext {

    public static void put(String key, Object value) {
        getSession().setAttribute(key, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        return (T) getSession().getAttribute(key);
    }

    public static void remove(String key) {
        getSession().removeAttribute(key);
    }

    public static Map<String, Object> getAll() {
        Map<String, Object> map = new HashMap<String, Object>();
        Enumeration<String> names = getSession().getAttributeNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            map.put(name, getSession().getAttribute(name));
        }
        return map;
    }

    public static void removeAll() {
        getSession().invalidate();
    }

    private static HttpSession getSession() {
        return DispatcherServlet.getSession();
    }
}
