package com.smart.framework.helper;

import com.smart.framework.util.PropsUtil;
import java.util.Properties;

public class ConfigHelper {

    private static final Properties configProps = PropsUtil.loadProps("config.properties");

    public static String getConfigString(String key) {
        return PropsUtil.getString(configProps, key);
    }

    public static int getConfigNumber(String key) {
        return PropsUtil.getNumber(configProps, key);
    }

    public static boolean getConfigBoolean(String key) {
        return PropsUtil.getBoolean(configProps, key);
    }
}
