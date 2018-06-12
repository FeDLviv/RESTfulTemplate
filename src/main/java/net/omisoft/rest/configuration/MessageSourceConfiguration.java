package net.omisoft.rest.configuration;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@AllArgsConstructor
public class MessageSourceConfiguration {

    private final MessageSource messageSource;

    public String getMessage(String id, Object[] args) {
        Locale currentLocale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(id, args, currentLocale);
    }

    public String getMessage(String id) {
        return getMessage(id, null);
    }

}
