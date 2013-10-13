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
            logger.error("加载 properties 文件出错！", e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                logger.error("加载 properties 文件出错！", e);
            }
        }
        return prop;
    }

    // 创建目录
    public static File createDir(String dirPath) {
        File dir = null;
        try {
            if (StringUtil.isNotEmpty(dirPath)) {
                dir = new File(dirPath);
                if (!dir.exists()) {
                    FileUtils.forceMkdir(dir);
                }
            }
        } catch (Exception e) {
            logger.error("创建目录出错！", e);
            throw new RuntimeException(e);
        }
        return dir;
    }

    // 复制目录（不会复制空目录）
    public static void copyDir(String srcPath, String destPath) {
        try {
            File src = new File(srcPath);
            File dest = new File(destPath);

            checkDir(src);
            checkDir(dest);

            FileUtils.copyDirectoryToDirectory(src, dest);
        } catch (Exception e) {
            logger.error("复制目录出错！", e);
            throw new RuntimeException(e);
        }
    }

    // 复制文件
    public static void copyFile(String srcPath, String destPath) {
        try {
            File src = new File(srcPath);
            File dest = new File(destPath);

            checkFile(src);
            checkDir(dest);

            FileUtils.copyFileToDirectory(src, dest);
        } catch (Exception e) {
            logger.error("复制文件出错！", e);
            throw new RuntimeException(e);
        }
    }

    // 删除目录
    public static void deleteDir(String dirPath) {
        try {
            File dir = new File(dirPath);

            checkDir(dir);

            FileUtils.deleteDirectory(dir);
        } catch (Exception e) {
            logger.error("删除目录出错！", e);
            throw new RuntimeException(e);
        }
    }

    // 删除文件
    public static void deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                if (file.isFile()) {
                    FileUtils.forceDelete(file);
                }
            }
        } catch (Exception e) {
            logger.error("删除文件出错！", e);
            throw new RuntimeException(e);
        }
    }

    private static void checkDir(File src) {
        if (!src.exists()) {
            throw new RuntimeException("该路径不存在！" + src);
        }
        if (!src.isDirectory()) {
            throw new RuntimeException("该路径不是目录！");
        }
    }

    private static void checkFile(File src) {
        if (!src.exists()) {
            throw new RuntimeException("该路径不存在！" + src);
        }
        if (!src.isFile()) {
            throw new RuntimeException("该路径不是文件！");
        }
    }
}
