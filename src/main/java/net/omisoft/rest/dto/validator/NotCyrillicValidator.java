package net.omisoft.rest.dto.validator;

import net.omisoft.rest.configuration.MessageSourceConfiguration;
import net.omisoft.rest.dto.validator.annotation.ValidateNotCyrillic;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotCyrillicValidator implements ConstraintValidator<ValidateNotCyrillic, String> {

    private static final String REGEX = ".*\\p{InCyrillic}.*";

    @Autowired
    private MessageSourceConfiguration message;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || !value.matches(REGEX)) {
            return true;
        } else {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message.getMessage(context.getDefaultConstraintMessageTemplate())).addConstraintViolation();
            return false;
        }
    }

}