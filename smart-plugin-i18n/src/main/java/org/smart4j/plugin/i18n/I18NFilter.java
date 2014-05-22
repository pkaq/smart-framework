package org.smart4j.plugin.i18n;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import org.smart4j.framework.FrameworkConstant;
import org.smart4j.framework.core.ConfigHelper;
import org.smart4j.framework.util.ClassUtil;
import org.smart4j.framework.util.StringUtil;
import org.smart4j.framework.util.WebUtil;

@WebFilter("/*")
public class I18NFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 获取请求路径
        HttpServletRequest req = (HttpServletRequest) request;
        String requestPath = WebUtil.getRequestPath(req);
        if (!requestPath.startsWith(FrameworkConstant.WWW_PATH)) {
            // 获取系统语言并放入 Request 中
            String systemLanguage = getSystemLanguage((HttpServletRequest) request);
            request.setAttribute(I18NConstant.SYSTEM_LANGUAGE, systemLanguage);
            // 判断是否重新
            boolean reloadable = ConfigHelper.getBoolean("smart.plugin.i18n.reloadable");
            if (reloadable) {
                // 清理 ResourceBundle 缓存
                ResourceBundle.clearCache();
                // 生成 JS 语言包
                String appBasePath = req.getServletContext().getRealPath("");
                I18NPlugin.generateJS(appBasePath);
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    private static String getSystemLanguage(HttpServletRequest request) {
        // 先从 Cookie 中获取系统语言
        String language = WebUtil.getCookie(request, I18NConstant.COOKIE_LANGUAGE);
        if (StringUtil.isEmpty(language)) {
            // 若为空，则获取浏览器首语言
            language = request.getLocale().toString();
            if (StringUtil.isEmpty(language)) {
                // 若为空，则获取操作系统语言
                language = Locale.getDefault().toString();
            }
        }
        // 若资源包不存在，则使用默认语言（英文）
        String i18nPropsPath = ClassUtil.getClassPath() + I18NConstant.I18N_DIR + "i18n_" + language + ".properties";
        File i18nPropsFile = new File(i18nPropsPath);
        if (!i18nPropsFile.exists()) {
            language = I18NConstant.DEFAULT_LANGUAGE;
        }
        return language;
    }
}
