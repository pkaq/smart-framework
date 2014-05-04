package org.smart4j.framework.core;

public interface FrameworkConstant {

    String UTF_8 = "UTF-8";

    String CONFIG_PROPS = "smart.properties";
    String SQL_PROPS = "smart-sql.properties";

    String PLUGIN_PACKAGE = "org.smart4j.plugin";

    String JSP_PATH = ConfigHelper.getString("app.jsp_path", "/WEB-INF/jsp/");
    String WWW_PATH = ConfigHelper.getString("app.www_path", "/www/");
    String HOME_PAGE = ConfigHelper.getString("app.home_page", "/index.html");
    int UPLOAD_LIMIT = ConfigHelper.getInt("app.upload_limit", 10);
}
