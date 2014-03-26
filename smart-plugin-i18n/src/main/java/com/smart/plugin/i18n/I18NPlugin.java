package com.smart.plugin.i18n;

import com.smart.framework.FrameworkConstant;
import com.smart.framework.Plugin;
import com.smart.framework.util.ArrayUtil;
import com.smart.framework.util.ClassUtil;
import com.smart.framework.util.FileUtil;
import com.smart.framework.util.JsonUtil;
import com.smart.framework.util.PropsUtil;
import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class I18NPlugin implements Plugin {

    @Override
    public void init() {
        // 生成 JS 语言包
        String appBasePath = ClassUtil.getClassPath() + "../../";
        generateJS(appBasePath);
    }

    @Override
    public void destroy() {
    }

    public static void generateJS(String appBasePath) {
        // 定义相关根路径
        String propsBasePath = appBasePath + "/WEB-INF/classes/" + I18NConstant.I18N_DIR;
        String jsBasePath = appBasePath + FrameworkConstant.WWW_PATH + I18NConstant.I18N_DIR;
        // 获取属性文件目录
        File propsBaseDir = new File(propsBasePath);
        if (propsBaseDir.exists()) {
            // 获取所有属性文件
            String[] propsFileNames = propsBaseDir.list();
            if (ArrayUtil.isNotEmpty(propsFileNames)) {
                // 遍历所有属性文件
                for (String propsFileName : propsFileNames) {
                    // 定义 JS 文件路径
                    String jsFilePath = jsBasePath + propsFileName.substring(0, propsFileName.lastIndexOf(".")) + ".js";
                    // 从属性文件中加载相关数据
                    Map<String, String> map = new HashMap<String, String>();
                    Properties props = PropsUtil.loadProps(I18NConstant.I18N_DIR + propsFileName);
                    Enumeration<?> names = props.propertyNames();
                    while (names.hasMoreElements()) {
                        String name = (String) names.nextElement();
                        String value = props.getProperty(name);
                        map.put(name, value);
                    }
                    // 将数据转换为 JSON 并写入 JS 文件
                    String jsFileContent = "window.I18N = " + JsonUtil.toJson(map) + ";";
                    FileUtil.writeFile(jsFilePath, jsFileContent);
                }
            }
        }
    }
}
