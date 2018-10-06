package net.omisoft.rest.dto.validator;

import net.omisoft.rest.configuration.MessageSourceConfiguration;
import net.omisoft.rest.dto.validator.annotation.ValidateEnum;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnumValidator implements ConstraintValidator<ValidateEnum, String> {

    @Autowired
    private MessageSourceConfiguration message;
    private List<String> list;
    private boolean ignoreCase;

    @Override
    public void initialize(ValidateEnum constraintAnnotation) {
        if (constraintAnnotation.enumeration().isEnum()) {
            list = Arrays.stream(constraintAnnotation.enumeration().getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.toList());
            list.removeAll(Arrays.asList(constraintAnnotation.exclude()));
            list.addAll(new ArrayList<>(Arrays.asList(constraintAnnotation.include())));
            ignoreCase = constraintAnnotation.ignoreCase();
            if (ignoreCase) {
                list.replaceAll(String::toLowerCase);
            }
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (list != null) {
            if (value == null) {
                return true;
            }
            if (ignoreCase) {
                if (list.stream().anyMatch(x -> x.equalsIgnoreCase(value))) {
                    return true;
                }
            } else {
                if (list.stream().anyMatch(x -> x.equals(value))) {
                    return true;
                }
            }
            context.disableDefaultConstraintViolation();
            String msg = message.getMessage(context.getDefaultConstraintMessageTemplate(),
                    new Object[]{String.join(", ", list)});
            context.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
            return false;
        } else {
            return false;
        }
    }

}