package com.smart.framework.helper;

import com.smart.framework.bean.Multipart;
import com.smart.framework.util.FileUtil;
import java.io.InputStream;
import java.util.List;

public class UploadHelper {

    public static void upload(String basePath, Multipart multipart) {
        String fileName = multipart.getFileName();
        InputStream inputStream = multipart.getInputStream();
        String filePath = basePath + fileName;
        FileUtil.uploadFile(filePath, inputStream);
    }

    public static void upload(String basePath, List<Multipart> multipartList) {
        for (Multipart multipart : multipartList) {
            upload(basePath, multipart);
        }
    }
}
