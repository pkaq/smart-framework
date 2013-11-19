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
@WebServlet(name = Constant.SERVLET_UPLOAD, urlPatterns = Constant.REQUEST_UPLOAD)
public class UploadServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取文件路径
        String pathName = request.getParameter(Constant.UPLOAD_PATH_NAME);
        String filePath = WebUtil.getUploadFilePath(request, Constant.UPLOAD_BASE_PATH + pathName);

        // 获取文件名
        Part part = request.getPart(Constant.UPLOAD_INPUT_NAME);
        String fileName = WebUtil.getUploadFileName(request, part);

        // 写入文件
        part.write(filePath + "/" + fileName);

        // 返回结果
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(Constant.UPLOAD_FILE_NAME, fileName);
        data.put(Constant.UPLOAD_FILE_TYPE, part.getContentType());
        data.put(Constant.UPLOAD_FILE_SIZE, part.getSize());
        WebUtil.writeJSON(response, new Result(true).data(data));
    }
}
