package com.smart.framework.bean;

import com.smart.framework.base.BaseBean;
import java.io.InputStream;

public class Multipart extends BaseBean {

    private String fileName;
    private long fileSize;
    private String contentType;
    private InputStream inputStream;

    public Multipart(String fileName, String contentType, long fileSize, InputStream inputStream) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.inputStream = inputStream;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public InputStream getInputStream() {
        return inputStream;
    }
}
