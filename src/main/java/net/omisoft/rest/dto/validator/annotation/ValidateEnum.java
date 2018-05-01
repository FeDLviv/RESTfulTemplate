package net.omisoft.rest.dto.validator.annotation;

import net.omisoft.rest.dto.validator.EnumValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
@Constraint(validatedBy = EnumValidator.class)
public @interface ValidateEnum {

    String message() default "exception.enum.wrong";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<?> enumeration();

}
