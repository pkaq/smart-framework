package com.smart.framework.bean;

import java.io.InputStream;

public class Multipart {

    private String fileName;
    private InputStream inputStream;

    public Multipart(String fileName, InputStream inputStream) {
        this.fileName = fileName;
        this.inputStream = inputStream;
    }

    public String getFileName() {
        return fileName;
    }

    public InputStream getInputStream() {
        return inputStream;
    }
}
