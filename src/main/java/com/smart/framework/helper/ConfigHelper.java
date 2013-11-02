package com.smart.framework.helper;

import com.smart.framework.util.FileUtil;
import com.smart.framework.util.StringUtil;
import java.util.Properties;
import org.apache.log4j.Logger;

public class ConfigHelper {

    private static final Logger logger = Logger.getLogger(ConfigHelper.class);

    private static final Properties configProperties = FileUtil.loadPropFile("config.properties");

    private static final ConfigHelper instance = new ConfigHelper();

    private ConfigHelper() {
    }

    public static ConfigHelper getInstance() {
        return instance;
    }

    public String getStringProperty(String key) {
        String value = "";
        if (configProperties.containsKey(key)) {
            value = configProperties.getProperty(key);
        } else {
            logger.error("无法在 config.properties 文件中获取属性：" + key);
        }
        return value;
    }

    public int getNumberProperty(String key) {
        int value = 0;
        String sValue = getStringProperty(key);
        if (StringUtil.isNumber(sValue)) {
            value = Integer.parseInt(sValue);
        }
        return value;
    }
}
