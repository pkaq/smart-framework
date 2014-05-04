package org.smart4j.plugin.druid;

import com.alibaba.druid.support.http.StatViewServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import org.smart4j.framework.core.ConfigHelper;
import org.smart4j.framework.plugin.WebPlugin;
import org.smart4j.framework.util.StringUtil;

/**
 * 用于注册 Druid 的相关 Servlet
 *
 * @author huangyong
 * @since 2.3
 */
public class DruidPlugin extends WebPlugin {

    private static final String DRUID_STAT_URL = "/druid/*";

    @Override
    public void register(ServletContext servletContext) {
        // 添加 StatViewServlet
        ServletRegistration.Dynamic druidServlet = servletContext.addServlet("DruidStatView", StatViewServlet.class);
        // 从配置文件中获取该 Servlet 的 URL 路径
        String druidStatUrl = ConfigHelper.getString("druid.stat.url");
        // 若该 URL 路径不存在，则使用默认值
        if (StringUtil.isEmpty(druidStatUrl)) {
            druidStatUrl = DRUID_STAT_URL;
        }
        // 向 Servlet 中添加 URL 路径
        druidServlet.addMapping(druidStatUrl);
    }
}
