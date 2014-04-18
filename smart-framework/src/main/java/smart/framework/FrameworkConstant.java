package smart.framework;

import smart.framework.helper.ConfigHelper;

public interface FrameworkConstant {

    String UTF_8 = "UTF-8";

    String CONFIG_PROPS = "smart.properties";
    String SQL_PROPS = "smart-sql.properties";

    String PLUGIN_PACKAGE = "smart.plugin";

    String JSP_PATH = ConfigHelper.getConfigString("app.jsp_path", "/WEB-INF/jsp/");
    String WWW_PATH = ConfigHelper.getConfigString("app.www_path", "/www/");
    String HOME_PAGE = ConfigHelper.getConfigString("app.home_page", "/index.html");
    int UPLOAD_LIMIT = ConfigHelper.getConfigNumber("app.upload_limit", 10);
}
