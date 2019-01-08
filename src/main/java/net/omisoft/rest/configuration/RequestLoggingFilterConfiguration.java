package net.omisoft.rest.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import static net.omisoft.rest.ApplicationConstants.API_V1_BASE_PATH;

@Configuration
public class RequestLoggingFilterConfiguration {

    @Bean
    public CommonsRequestLoggingFilter loggingFilter() {
        CommonsRequestLoggingFilter filter
                = new CommonsRequestLoggingFilter();
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(1000);
        filter.setIncludeClientInfo(true);
        filter.setIncludeQueryString(false);
        filter.setIncludeHeaders(false);
        filter.setBeforeMessagePrefix("---> Request INFO: ");
        filter.setBeforeMessageSuffix("");
        filter.setAfterMessagePrefix("---> Request DATA: ");
        filter.setAfterMessageSuffix("");
        return filter;
    }

    @Bean
    public FilterRegistrationBean loggingFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean<>(loggingFilter());
        registration.addUrlPatterns(API_V1_BASE_PATH + "*");
        return registration;
    }

}
