package com.smart.framework.helper;

import com.smart.framework.FrameworkConstant;
import com.smart.framework.util.PropsUtil;
import java.util.Map;
import java.util.Properties;

public class ConfigHelper {

    private static final Properties configProps = PropsUtil.loadProps(FrameworkConstant.CONFIG_PROPS);

    public static String getConfigString(String key) {
        return PropsUtil.getString(configProps, key);
    }

    public static String getConfigString(String key, String defaultValue) {
        return PropsUtil.getString(configProps, key, defaultValue);
    }

    public static int getConfigNumber(String key) {
        return PropsUtil.getNumber(configProps, key);
    }

    public static int getConfigNumber(String key, int defaultValue) {
        return PropsUtil.getNumber(configProps, key, defaultValue);
    }

    public static boolean getConfigBoolean(String key) {
        return PropsUtil.getBoolean(configProps, key);
    }

    public static boolean getConfigBoolean(String key, boolean defaultValue) {
        return PropsUtil.getBoolean(configProps, key, defaultValue);
    }

    public static Map<String, Object> getConfigMap(String prefix) {
        return PropsUtil.getMap(configProps, prefix);
    }
}
