package org.smart4j.framework.core;

public interface FrameworkConstant {

    String UTF_8 = "UTF-8";

    String CONFIG_PROPS = "smart4j.properties";
    String SQL_PROPS = "smart4j-sql.properties";

    String PLUGIN_PACKAGE = "org.smart4j.plugin";

    String JSP_PATH = ConfigHelper.getConfigString("app.jsp_path", "/WEB-INF/jsp/");
    String WWW_PATH = ConfigHelper.getConfigString("app.www_path", "/www/");
    String HOME_PAGE = ConfigHelper.getConfigString("app.home_page", "/index.html");
    int UPLOAD_LIMIT = ConfigHelper.getConfigNumber("app.upload_limit", 10);
}
