package org.smart4j.sso.init;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.util.AssertionThreadLocalFilter;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;
import org.smart4j.sso.tool.SmartProps;

public class SmartInitializerImpl implements SmartInitializer {

    @Override
    public void init(ServletContext servletContext) {
        if (SmartProps.isSSO()) {
            String casServerUrlPrefix = SmartProps.getCasServerUrlPrefix();
            String casServerLoginUrl = SmartProps.getCasServerLoginUrl();
            String serverName = SmartProps.getServerName();
            String filterMapping = SmartProps.getFilterMapping();

            servletContext.addListener(SingleSignOutHttpSessionListener.class);

            FilterRegistration.Dynamic singleSignOutFilter = servletContext.addFilter("SingleSignOutFilter", SingleSignOutFilter.class);
            singleSignOutFilter.addMappingForUrlPatterns(null, false, filterMapping);

            FilterRegistration.Dynamic authenticationFilter = servletContext.addFilter("AuthenticationFilter", AuthenticationFilter.class);
            authenticationFilter.setInitParameter("casServerLoginUrl", casServerLoginUrl);
            authenticationFilter.setInitParameter("serverName", serverName);
            authenticationFilter.addMappingForUrlPatterns(null, false, filterMapping);

            FilterRegistration.Dynamic ticketValidationFilter = servletContext.addFilter("TicketValidationFilter", Cas20ProxyReceivingTicketValidationFilter.class);
            ticketValidationFilter.setInitParameter("casServerUrlPrefix", casServerUrlPrefix);
            ticketValidationFilter.setInitParameter("serverName", SmartProps.getServerName());
            ticketValidationFilter.addMappingForUrlPatterns(null, false, filterMapping);

            FilterRegistration.Dynamic requestWrapperFilter = servletContext.addFilter("RequestWrapperFilter", HttpServletRequestWrapperFilter.class);
            requestWrapperFilter.addMappingForUrlPatterns(null, false, filterMapping);

            FilterRegistration.Dynamic assertionThreadLocalFilter = servletContext.addFilter("AssertionThreadLocalFilter", AssertionThreadLocalFilter.class);
            assertionThreadLocalFilter.addMappingForUrlPatterns(null, false, filterMapping);
        }
    }
}
