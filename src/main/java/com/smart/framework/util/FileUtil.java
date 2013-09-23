package com.smart.framework.util;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class FileUtil {

    private static final Logger logger = Logger.getLogger(FileUtil.class);

    // 加载 properties 文件
    public static Properties loadPropFile(String propPath) {
        Properties prop = new Properties();
        InputStream is = null;
        try {
            String suffix = ".properties";
            if (propPath.lastIndexOf(suffix) == -1) {
                propPath += suffix;
            }
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(propPath);
            if (is != null) {
                prop.load(is);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return prop;
    }

    // 创建目录
    public static File createPath(String dirPath) {
        File dir = null;
        try {
            if (StringUtil.isNotEmpty(dirPath)) {
                dir = new File(dirPath);
                if (!dir.exists()) {
                    FileUtils.forceMkdir(dir);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        return dir;
    }
}
