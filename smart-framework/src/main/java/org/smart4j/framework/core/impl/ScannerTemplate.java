package org.smart4j.framework.core.impl;

import java.io.File;
import java.io.FileFilter;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.framework.util.ClassUtil;
import org.smart4j.framework.util.StringUtil;

public abstract class ScannerTemplate {

    private static final Logger logger = LoggerFactory.getLogger(ScannerTemplate.class);

    protected String packageName;
    protected Class<? extends Annotation> annotationClass;
    protected Class<?> superClass;

    protected ScannerTemplate(String packageName) {
        this.packageName = packageName;
    }

    protected ScannerTemplate addAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
        return this;
    }

    protected ScannerTemplate addSupperClass(Class<?> superClass) {
        this.superClass = superClass;
        return this;
    }

    public final List<Class<?>> getClassList() {
        List<Class<?>> classList = new ArrayList<Class<?>>();
        try {
            Enumeration<URL> urls = ClassUtil.getClassLoader().getResources(packageName.replace(".", "/"));
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    String protocol = url.getProtocol();
                    if (protocol.equals("file")) {
                        String packagePath = url.getPath();
                        addClass(classList, packagePath, packageName);
                    } else if (protocol.equals("jar")) {
                        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                        JarFile jarFile = jarURLConnection.getJarFile();
                        Enumeration<JarEntry> jarEntries = jarFile.entries();
                        while (jarEntries.hasMoreElements()) {
                            JarEntry jarEntry = jarEntries.nextElement();
                            String jarEntryName = jarEntry.getName();
                            if (jarEntryName.endsWith(".class")) {
                                String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                                Class<?> cls = ClassUtil.loadClass(className, false);
                                if (checkAddClass(cls)) {
                                    classList.add(cls);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("获取类出错！", e);
            throw new RuntimeException(e);
        }
        return classList;
    }

    private void addClass(List<Class<?>> classList, String packagePath, String packageName) {
        try {
            File[] files = new File(packagePath).listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
                }
            });
            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName();
                    if (file.isFile()) {
                        String className = fileName.substring(0, fileName.lastIndexOf("."));
                        if (StringUtil.isNotEmpty(packageName)) {
                            className = packageName + "." + className;
                        }
                        Class<?> cls = ClassUtil.loadClass(className, false);
                        if (checkAddClass(cls)) {
                            classList.add(cls);
                        }
                    } else {
                        String subPackagePath = fileName;
                        if (StringUtil.isNotEmpty(packagePath)) {
                            subPackagePath = packagePath + "/" + subPackagePath;
                        }
                        String subPackageName = fileName;
                        if (StringUtil.isNotEmpty(packageName)) {
                            subPackageName = packageName + "." + subPackageName;
                        }
                        addClass(classList, subPackagePath, subPackageName);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("添加类出错！", e);
            throw new RuntimeException(e);
        }
    }

    public abstract boolean checkAddClass(Class<?> cls);
}
