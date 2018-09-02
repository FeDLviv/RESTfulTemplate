package net.omisoft.rest.dto.validator;

import lombok.RequiredArgsConstructor;
import net.omisoft.rest.configuration.MessageSourceConfiguration;
import net.omisoft.rest.dto.validator.annotation.ValidateEnum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class EnumValidator implements ConstraintValidator<ValidateEnum, String> {

    private final MessageSourceConfiguration message;
    private List<String> list;

    @Override
    public void initialize(ValidateEnum constraintAnnotation) {
        if (constraintAnnotation.enumeration().isEnum()) {
            list = Arrays.stream(constraintAnnotation.enumeration().getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (list != null) {
            if (value == null || list.stream().anyMatch(x -> x.equals(value))) {
                return true;
            } else {
                context.disableDefaultConstraintViolation();
                String msg = message.getMessage(context.getDefaultConstraintMessageTemplate(),
                        new Object[]{String.join(", ", list)});
                context.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
                return false;
            }
        } else {
            return false;
        }
    }

}