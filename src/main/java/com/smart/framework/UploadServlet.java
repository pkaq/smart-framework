package com.smart.framework;

import com.smart.framework.bean.Result;
import com.smart.framework.util.WebUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@MultipartConfig
@WebServlet(name = "upload", urlPatterns = "/upload.do")
public class UploadServlet extends HttpServlet {

    private static final String UPLOAD_BASE_PATH = "www/upload/"; // 文件上传基础路径
    private static final String UPLOAD_RELATIVE_PATH = "path";    // 文件上传相对路径
    private static final String UPLOAD_FILE_NAME = "file";        // 文件标签的 file 名称

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取文件路径
        String relativePath = request.getParameter(UPLOAD_RELATIVE_PATH);
        String filePath = WebUtil.getFilePath(request, UPLOAD_BASE_PATH + relativePath);

        // 获取文件名
        Part part = request.getPart(UPLOAD_FILE_NAME);
        String fileName = WebUtil.getFileName(request, part);

        // 写入文件
        part.write(filePath + "/" + fileName);

        // 返回结果
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("fileName", fileName);
        data.put("fileType", part.getContentType());
        data.put("fileSize", part.getSize());
        WebUtil.writeJSON(response, new Result(true).data(data));
    }
}
