package com.smart.framework.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import org.apache.log4j.Logger;

public class StreamUtil {

    private static final Logger logger = Logger.getLogger(StreamUtil.class);

    // 从输入流复制到输出流
    public static void copyStream(InputStream input, OutputStream output) {
        try {
            int length;
            byte[] buffer = new byte[1024];
            while ((length = input.read(buffer, 0, buffer.length)) != -1) {
                output.write(buffer, 0, length);
            }
            output.flush();
        } catch (Exception e) {
            logger.error("复制流出错！", e);
            throw new RuntimeException(e);
        }
    }

    // 从输入流中返回字符串
    public static String toString(InputStream is) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            logger.error("Stream 转 String 出错！", e);
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
}
