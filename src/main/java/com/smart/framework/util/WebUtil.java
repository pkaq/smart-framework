package com.smart.framework.util;

import com.smart.framework.FrameworkConstant;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

public class WebUtil {

    private static final Logger logger = Logger.getLogger(WebUtil.class);

    // 将数据以纯文本格式写入响应中
    public static void writeText(HttpServletResponse response, Object data) {
        try {
            // 设置响应头
            response.setContentType("text/plain"); // 指定内容类型为纯文本格式
            response.setCharacterEncoding(FrameworkConstant.DEFAULT_CHARSET); // 防止中文乱码

            // 向响应中写入数据
            PrintWriter writer = response.getWriter();
            writer.write(data + ""); // 转为字符串
        } catch (Exception e) {
            logger.error("在响应中写数据出错！", e);
            throw new RuntimeException(e);
        }
    }

    // 将数据以 JSON 格式写入响应中
    public static void writeJSON(HttpServletResponse response, Object data) {
        try {
            // 设置响应头
            response.setContentType("application/json"); // 指定内容类型为 JSON 格式
            response.setCharacterEncoding(FrameworkConstant.DEFAULT_CHARSET); // 防止中文乱码

            // 向响应中写入数据
            PrintWriter writer = response.getWriter();
            writer.write(JSONUtil.toJSON(data)); // 转为 JSON 字符串
        } catch (Exception e) {
            logger.error("在响应中写数据出错！", e);
            throw new RuntimeException(e);
        }
    }

    // 将数据以 HTML 格式写入响应中（在 JS 中获取的是 JSON 字符串，而不是 JSON 对象）
    public static void writeHTML(HttpServletResponse response, Object data) {
        try {
            // 设置响应头
            response.setContentType("text/html"); // 指定内容类型为 HTML 格式
            response.setCharacterEncoding(FrameworkConstant.DEFAULT_CHARSET); // 防止中文乱码

            // 向响应中写入数据
            PrintWriter writer = response.getWriter();
            writer.write(JSONUtil.toJSON(data)); // 转为 JSON 字符串
        } catch (Exception e) {
            logger.error("在响应中写数据出错！", e);
            throw new RuntimeException(e);
        }
    }

    // 获取上传文件路径
    public static String getUploadFilePath(HttpServletRequest request, String relativePath) {
        // 返回绝对路径
        return request.getServletContext().getRealPath("") + relativePath;
    }

    // 获取上传文件名
    public static String getUploadFileName(HttpServletRequest request, Part part) {
        // 防止中文乱码（可放在 EncodingFilter 中处理）
//        request.setCharacterEncoding(Constant.DEFAULT_CHARSET);

        // 从请求头中获取文件名
        String cd = part.getHeader("Content-Disposition");
        String fileName = cd.substring(cd.lastIndexOf("=") + 2, cd.length() - 1);

        // 解决 IE 浏览器文件名问题
        if (fileName.contains("\\")) {
            fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
        }

        return fileName;
    }

    // 从请求中获取所有参数（当参数名重复时，用后者覆盖前者）
    public static Map<String, String> getRequestParamMap(HttpServletRequest request) {
        Map<String, String> paramMap = new LinkedHashMap<String, String>();
        try {
            String method = request.getMethod();
            if (method.equalsIgnoreCase("put") || method.equalsIgnoreCase("delete")) {
                String queryString = CodecUtil.decodeUTF8(StreamUtil.getString(request.getInputStream()));
                if (StringUtil.isNotEmpty(queryString)) {
                    String[] qsArray = StringUtil.splitString(queryString, "&");
                    if (ArrayUtil.isNotEmpty(qsArray)) {
                        for (String qs : qsArray) {
                            String[] array = StringUtil.splitString(qs, "=");
                            if (ArrayUtil.isNotEmpty(array) && array.length == 2) {
                                String paramName = array[0];
                                String paramValue = array[1];
                                if (checkParamName(paramName)) {
                                    paramMap.put(paramName, paramValue);
                                }
                            }
                        }
                    }
                }
            } else {
                Enumeration<String> paramNames = request.getParameterNames();
                while (paramNames.hasMoreElements()) {
                    String paramName = paramNames.nextElement();
                    if (checkParamName(paramName)) {
                        String[] paramValues = request.getParameterValues(paramName);
                        if (ArrayUtil.isNotEmpty(paramValues)) {
                            if (paramValues.length == 1) {
                                paramMap.put(paramName, paramValues[0]);
                            } else {
                                StringBuilder paramValue = new StringBuilder("");
                                for (int i = 0; i < paramValues.length; i++) {
                                    paramValue.append(paramValues[i]);
                                    if (i != paramValues.length - 1) {
                                        paramValue.append(StringUtil.SEPARATOR);
                                    }
                                }
                                paramMap.put(paramName, paramValue.toString());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("获取请求参数出错！", e);
            throw new RuntimeException(e);
        }
        return paramMap;
    }

    private static boolean checkParamName(String paramName) {
        return !paramName.equals("_"); // 忽略 jQuery 缓存参数
    }

    // 创建查询映射（查询字符串格式：a:1;b:2）
    public static Map<String, String> createQueryMap(String queryString) {
        Map<String, String> queryMap = new HashMap<String, String>();
        if (StringUtil.isNotEmpty(queryString)) {
            String[] queryArray = queryString.split(";");
            if (ArrayUtil.isNotEmpty(queryArray)) {
                for (String query : queryArray) {
                    String[] fieldArray = query.split(":");
                    if (ArrayUtil.isNotEmpty(fieldArray) && fieldArray.length == 2) {
                        queryMap.put(fieldArray[0], fieldArray[1]);
                    }
                }
            }
        }
        return queryMap;
    }

    // 转发请求
    public static void forwardRequest(String path, HttpServletRequest request, HttpServletResponse response) {
        try {
            request.getRequestDispatcher(path).forward(request, response);
        } catch (Exception e) {
            logger.error("转发请求出错！", e);
            throw new RuntimeException(e);
        }
    }

    // 重定向请求
    public static void redirectRequest(String path, HttpServletRequest request, HttpServletResponse response) {
        try {
            response.sendRedirect(request.getContextPath() + path);
        } catch (Exception e) {
            logger.error("重定向请求出错！", e);
            throw new RuntimeException(e);
        }
    }

    // 发送错误代码
    public static void sendError(int code, HttpServletResponse response) {
        try {
            response.sendError(code);
        } catch (Exception e) {
            logger.error("发送错误代码出错！", e);
            throw new RuntimeException(e);
        }
    }

    // 判断是否为 AJAX 请求
    public static boolean isAJAX(HttpServletRequest request) {
        return request.getHeader("X-Requested-With") != null;
    }

    // 获取请求路径
    public static String getRequestPath(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        String pathInfo = StringUtil.defaultIfEmpty(request.getPathInfo(), "");
        return servletPath + pathInfo;
    }

    // 将数据放入 Cookie 中
    public static void addCookie(HttpServletResponse response, String name, String value, String domain, int expires) {
        try {
            if (StringUtil.isNotEmpty(name)) {
                value = CodecUtil.encodeUTF8(value);
                Cookie cookie = new Cookie(name, value);
                cookie.setDomain(domain);
                cookie.setMaxAge(expires);
                response.addCookie(cookie);
            }
        } catch (Exception e) {
            logger.error("添加 Cookie 出错！", e);
            throw new RuntimeException(e);
        }
    }

    // 从 Cookie 中获取数据
    public static String getCookie(HttpServletRequest request, String name) {
        String value = "";
        try {
            Cookie[] cookieArray = request.getCookies();
            if (cookieArray != null) {
                for (Cookie cookie : cookieArray) {
                    if (StringUtil.isNotEmpty(name) && name.equals(cookie.getName())) {
                        value = CodecUtil.decodeUTF8(cookie.getValue());
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("获取 Cookie 出错！", e);
            throw new RuntimeException(e);
        }
        return value;
    }

    // 获取 URL 内容
    public static String getURLContent(String url) {
        String content;
        try {
            InputStream is = new URL(url).openStream();
            content = StreamUtil.getString(is);
        } catch (Exception e) {
            logger.error("获取 URL 内容出错！", e);
            throw new RuntimeException(e);
        }
        return content;
    }

    // 下载文件
    public static void downloadFile(HttpServletResponse response, String filePath) {
        try {
            String originalFileName = FilenameUtils.getName(filePath);
            String decodedFileName = FileUtil.getDecodedFileName(originalFileName);
            String downloadedFileName = new String(decodedFileName.getBytes(), "ISO-8859-1");

            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment;filename=" + downloadedFileName);

            InputStream inputStream = new BufferedInputStream(new FileInputStream(filePath));
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            StreamUtil.copyStream(inputStream, outputStream);
        } catch (Exception e) {
            logger.error("下载文件出错！", e);
            throw new RuntimeException(e);
        }
    }
}
