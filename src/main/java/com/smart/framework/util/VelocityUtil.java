package com.smart.framework.util;

import java.io.FileWriter;
import java.io.StringWriter;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;

public class VelocityUtil {

    private static final Logger logger = Logger.getLogger(VelocityUtil.class);

    private static final VelocityEngine engine = new VelocityEngine();
    private static final String UTF_8 = "UTF-8";

    static {
        engine.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, ClassUtil.getClassPath());
        engine.setProperty(Velocity.ENCODING_DEFAULT, UTF_8);
        engine.setProperty(Velocity.INPUT_ENCODING, UTF_8);
        engine.setProperty(Velocity.OUTPUT_ENCODING, UTF_8);
        engine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, NullLogChute.class.getName());
    }

    // 设置 VM 文件加载路径（默认为 classpath）
    public static void setVmLoaderPath(String path) {
        engine.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, path);
    }

    // 合并模板到文件中
    public static void mergeTemplateIntoFile(String vmPath, Map<String, Object> dataMap, String filePath) {
        try {
            FileUtil.createFile(filePath);

            Template template = engine.getTemplate(vmPath);
            VelocityContext context = new VelocityContext(dataMap);
            FileWriter writer = new FileWriter(filePath);
            template.merge(context, writer);

            writer.close();
        } catch (Exception e) {
            logger.error("合并模板出错！", e);
            throw new RuntimeException(e);
        }
    }

    // 合并模板并返回字符串
    public static String mergeTemplateReturnString(String vmPath, Map<String, Object> dataMap) {
        String result;
        try {
            Template template = engine.getTemplate(vmPath);
            VelocityContext context = new VelocityContext(dataMap);
            StringWriter writer = new StringWriter();
            template.merge(context, writer);
            result = writer.toString();

            writer.close();
        } catch (Exception e) {
            logger.error("合并模板出错！", e);
            throw new RuntimeException(e);
        }
        return result;
    }
}
