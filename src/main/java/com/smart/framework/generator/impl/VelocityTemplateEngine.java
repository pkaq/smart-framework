package com.smart.framework.generator.impl;

import com.smart.framework.FrameworkConstant;
import com.smart.framework.generator.TemplateEngine;
import com.smart.framework.util.ClassUtil;
import com.smart.framework.util.FileUtil;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VelocityTemplateEngine implements TemplateEngine {

    private static final Logger logger = LoggerFactory.getLogger(VelocityTemplateEngine.class);

    private final VelocityEngine velocityEngine = new VelocityEngine();

    public VelocityTemplateEngine() {
        init(ClassUtil.getClassPath());
    }

    public VelocityTemplateEngine(String templateLoaderPath) {
        init(templateLoaderPath);
    }

    private void init(String templateLoaderPath) {
        velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, NullLogChute.class.getName());
        velocityEngine.setProperty(Velocity.INPUT_ENCODING, FrameworkConstant.DEFAULT_CHARSET);
        velocityEngine.setProperty(Velocity.OUTPUT_ENCODING, FrameworkConstant.DEFAULT_CHARSET);
        velocityEngine.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, templateLoaderPath);
        velocityEngine.init();
    }

    @Override
    public void generateDocument(String templatePath, Map<String, Object> dataMap, String documentPath) {
        Context context = new VelocityContext(dataMap);
        Writer writer = null;
        try {
            FileUtil.createFile(documentPath);
            writer = new FileWriter(documentPath);
            velocityEngine.mergeTemplate(templatePath, FrameworkConstant.DEFAULT_CHARSET, context, writer);
        } catch (Exception e) {
            logger.error("处理模板出错！", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    logger.error("释放资源出错！", e);
                }
            }
        }
    }

    @Override
    public String generateString(String templateString, Map<String, Object> dataMap) {
        String result = "";
        Context context = new VelocityContext(dataMap);
        Writer writer = null;
        try {
            writer = new StringWriter();
            if (velocityEngine.evaluate(context, writer, "", templateString)) {
                result = writer.toString();
            }
        } catch (Exception e) {
            logger.error("处理模板出错！", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    logger.error("释放资源出错！", e);
                }
            }
        }
        return result;
    }
}
