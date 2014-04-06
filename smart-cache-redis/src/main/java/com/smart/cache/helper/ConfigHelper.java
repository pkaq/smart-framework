package com.smart.cache.helper;

import com.smart.cache.util.PropsUtil;

import java.util.Properties;

public class ConfigHelper {

    private static final Properties configProps = PropsUtil.loadProps("config.properties");

    public static String getConfigString(String key) {
        return PropsUtil.getString(configProps, key);
    }

}