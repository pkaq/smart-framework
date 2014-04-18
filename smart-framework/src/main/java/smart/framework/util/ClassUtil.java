package smart.framework.util;

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

public class ClassUtil {

    private static final Logger logger = LoggerFactory.getLogger(ClassUtil.class);

    // 获取类加载器
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    // 获取类路径
    public static String getClassPath() {
        String classpath = "";
        URL resource = getClassLoader().getResource("");
        if (resource != null) {
            classpath = resource.getPath();
        }
        return classpath;
    }

    // 加载类
    public static Class<?> loadClass(String className, boolean isInitialized) {
        Class<?> cls;
        try {
            cls = Class.forName(className, isInitialized, getClassLoader());
        } catch (ClassNotFoundException e) {
            logger.error("加载类出错！", e);
            throw new RuntimeException(e);
        }
        return cls;
    }

    // 获取指定包名下的所有类
    public static List<Class<?>> getClassList(String packageName) {
        List<Class<?>> classList = new ArrayList<Class<?>>();
        try {
            Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".", "/"));
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
                                if (className.substring(0, className.lastIndexOf(".")).equals(packageName)) {
                                    classList.add(loadClass(className, false));
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

    // 获取指定包名下指定注解的所有类
    public static List<Class<?>> getClassListByAnnotation(String packageName, Class<? extends Annotation> annotationClass) {
        List<Class<?>> classList = new ArrayList<Class<?>>();
        try {
            Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".", "/"));
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    String protocol = url.getProtocol();
                    if (protocol.equals("file")) {
                        String packagePath = url.getPath();
                        addClassByAnnotation(classList, packagePath, packageName, annotationClass);
                    } else if (protocol.equals("jar")) {
                        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                        JarFile jarFile = jarURLConnection.getJarFile();
                        Enumeration<JarEntry> jarEntries = jarFile.entries();
                        while (jarEntries.hasMoreElements()) {
                            JarEntry jarEntry = jarEntries.nextElement();
                            String jarEntryName = jarEntry.getName();
                            if (jarEntryName.endsWith(".class")) {
                                String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                                Class<?> cls = loadClass(className, false);
                                if (cls.isAnnotationPresent(annotationClass)) {
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

    // 获取指定包名下指定父类的所有类
    public static List<Class<?>> getClassListBySuper(String packageName, Class<?> superClass) {
        List<Class<?>> classList = new ArrayList<Class<?>>();
        try {
            Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".", "/"));
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    String protocol = url.getProtocol();
                    if (protocol.equals("file")) {
                        String packagePath = url.getPath();
                        addClassBySuper(classList, packagePath, packageName, superClass);
                    } else if (protocol.equals("jar")) {
                        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                        JarFile jarFile = jarURLConnection.getJarFile();
                        Enumeration<JarEntry> jarEntries = jarFile.entries();
                        while (jarEntries.hasMoreElements()) {
                            JarEntry jarEntry = jarEntries.nextElement();
                            String jarEntryName = jarEntry.getName();
                            if (jarEntryName.endsWith(".class")) {
                                String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                                Class<?> cls = loadClass(className, false);
                                if (superClass.isAssignableFrom(cls) && !superClass.equals(cls)) {
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

    private static void addClass(List<Class<?>> classList, String packagePath, String packageName) {
        try {
            File[] files = getClassFiles(packagePath);
            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName();
                    if (file.isFile()) {
                        String className = getClassName(packageName, fileName);
                        classList.add(loadClass(className, false));
                    } else {
                        String subPackagePath = getSubPackagePath(packagePath, fileName);
                        String subPackageName = getSubPackageName(packageName, fileName);
                        addClass(classList, subPackagePath, subPackageName);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("添加类出错！", e);
            throw new RuntimeException(e);
        }
    }

    private static File[] getClassFiles(String packagePath) {
        return new File(packagePath).listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
            }
        });
    }

    private static String getClassName(String packageName, String fileName) {
        String className = fileName.substring(0, fileName.lastIndexOf("."));
        if (StringUtil.isNotEmpty(packageName)) {
            className = packageName + "." + className;
        }
        return className;
    }

    private static String getSubPackagePath(String packagePath, String filePath) {
        String subPackagePath = filePath;
        if (StringUtil.isNotEmpty(packagePath)) {
            subPackagePath = packagePath + "/" + subPackagePath;
        }
        return subPackagePath;
    }

    private static String getSubPackageName(String packageName, String filePath) {
        String subPackageName = filePath;
        if (StringUtil.isNotEmpty(packageName)) {
            subPackageName = packageName + "." + subPackageName;
        }
        return subPackageName;
    }

    private static void addClassByAnnotation(List<Class<?>> classList, String packagePath, String packageName, Class<? extends Annotation> annotationClass) {
        try {
            File[] files = getClassFiles(packagePath);
            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName();
                    if (file.isFile()) {
                        String className = getClassName(packageName, fileName);
                        Class<?> cls = loadClass(className, false);
                        if (cls.isAnnotationPresent(annotationClass)) {
                            classList.add(cls);
                        }
                    } else {
                        String subPackagePath = getSubPackagePath(packagePath, fileName);
                        String subPackageName = getSubPackageName(packageName, fileName);
                        addClassByAnnotation(classList, subPackagePath, subPackageName, annotationClass);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("添加类出错！", e);
            throw new RuntimeException(e);
        }
    }

    private static void addClassBySuper(List<Class<?>> classList, String packagePath, String packageName, Class<?> superClass) {
        try {
            File[] files = getClassFiles(packagePath);
            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName();
                    if (file.isFile()) {
                        String className = getClassName(packageName, fileName);
                        Class<?> cls = loadClass(className, false);
                        if (superClass.isAssignableFrom(cls) && !superClass.equals(cls)) {
                            classList.add(cls);
                        }
                    } else {
                        String subPackagePath = getSubPackagePath(packagePath, fileName);
                        String subPackageName = getSubPackageName(packageName, fileName);
                        addClassBySuper(classList, subPackagePath, subPackageName, superClass);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("添加类出错！", e);
            throw new RuntimeException(e);
        }
    }
}
