package net.omisoft.rest.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

import static net.omisoft.rest.ApplicationConstants.LANGUAGE_HEADER;

@Configuration
public class MultilanguageConfiguration {

    @Bean
    public LocaleResolver localeResolver() {
        return new CookieLocaleResolver() {
            @Override
            public Locale resolveLocale(HttpServletRequest request) {
                String acceptLanguage = request.getHeader(LANGUAGE_HEADER);
                if (acceptLanguage == null || acceptLanguage.trim().isEmpty()) {
                    return super.determineDefaultLocale(request);
                }
                return request.getLocale();
            }
        };
    }

}

