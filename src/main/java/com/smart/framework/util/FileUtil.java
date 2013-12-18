package com.smart.framework.util;

import com.smart.framework.FrameworkConstant;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    // 加载 properties 文件
    public static Properties loadPropsFile(String propPath) {
        Properties props = new Properties();
        InputStream is = null;
        try {
            String suffix = ".properties";
            if (propPath.lastIndexOf(suffix) == -1) {
                propPath += suffix;
            }
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(propPath);
            if (is != null) {
                props.load(is);
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
        return props;
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

    // 创建文件
    public static File createFile(String filePath) {
        File file = null;
        try {
            if (StringUtil.isNotEmpty(filePath)) {
                file = new File(filePath);
                File parentDir = file.getParentFile();
                if (!parentDir.exists()) {
                    FileUtils.forceMkdir(parentDir);
                }
            }
        } catch (Exception e) {
            logger.error("创建文件出错！", e);
            throw new RuntimeException(e);
        }
        return file;
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

            if (dir.exists() && dir.isDirectory()) {
                FileUtils.deleteDirectory(dir);
            }
        } catch (Exception e) {
            logger.error("删除目录出错！", e);
            throw new RuntimeException(e);
        }
    }

    // 删除文件
    public static void deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                FileUtils.forceDelete(file);
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

    // 将字符串写入文件
    public static void writeFile(String filePath, String fileContent) {
        OutputStream os = null;
        Writer w = null;
        try {
            FileUtil.createFile(filePath);
            os = new BufferedOutputStream(new FileOutputStream(filePath));
            w = new OutputStreamWriter(os, FrameworkConstant.DEFAULT_CHARSET);
            w.write(fileContent);
            w.flush();
        } catch (Exception e) {
            logger.error("写入文件出错！", e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (w != null) {
                    w.close();
                }
            } catch (Exception e) {
                logger.error("释放资源出错！", e);
            }
        }
    }

    // 获取真实文件名（去掉文件路径）
    public static String getRealFileName(String fileName) {
        return FilenameUtils.getName(fileName);
    }

    // 获取编码后的文件名（将文件名进行 Base64 编码）
    public static String getEncodedFileName(String fileName) {
        String prefix = FilenameUtils.getBaseName(fileName);
        String suffix = FilenameUtils.getExtension(fileName);
        return CodecUtil.encodeBase64(prefix) + "." + suffix;
    }

    // 获取解码后的文件名（将文件名进行 Base64 解码）
    public static String getDecodedFileName(String fileName) {
        String prefix = FilenameUtils.getBaseName(fileName);
        String suffix = FilenameUtils.getExtension(fileName);
        return CodecUtil.decodeBase64(prefix) + "." + suffix;
    }

    // 判断文件是否存在
    public static boolean checkFileExists(String filePath) {
        return new File(filePath).exists();
    }
}
