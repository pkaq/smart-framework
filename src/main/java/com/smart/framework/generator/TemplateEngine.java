package com.smart.framework.generator;

import java.util.Map;

public interface TemplateEngine {

    /**
     * 生成目标文件（解析模板文件）
     *
     * @param templateFilePath 模板文件路径（相对路径）
     * @param templateDataMap  模板数据映射
     * @param targetFilePath   目标文件路径（绝对路径）
     */
    void generateDocument(String templateFilePath, Map<String, Object> templateDataMap, String targetFilePath);

    /**
     * 生成目标字符串（解析模板字符串）
     *
     * @param templateString  模板字符串
     * @param templateDataMap 模板数据映射
     * @return 目标字符串
     */
    String generateString(String templateString, Map<String, Object> templateDataMap);
}
