package com.smart.framework.helper;

import com.smart.framework.util.CastUtil;
import com.smart.framework.util.FileUtil;
import com.smart.framework.util.StringUtil;
import java.util.Properties;

public class ConfigHelper {

    private static final Properties configProps = FileUtil.loadPropsFile("config.properties");

    public static String getStringProperty(String key) {
        String value = "";
        if (configProps.containsKey(key)) {
            value = configProps.getProperty(key);
        }
        return value;
    }

    public static int getNumberProperty(String key) {
        int value = 0;
        String sValue = getStringProperty(key);
        if (StringUtil.isNumber(sValue)) {
            value = CastUtil.castInt(sValue);
        }
        return value;
    }

    public static boolean getBooleanProperty(String key) {
        boolean value = false;
        if (configProps.containsKey(key)) {
            value = CastUtil.castBoolean(configProps.getProperty(key));
        }
        return value;
    }
}
