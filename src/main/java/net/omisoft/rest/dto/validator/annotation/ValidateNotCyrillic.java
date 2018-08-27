package net.omisoft.rest.dto.validator.annotation;

import net.omisoft.rest.dto.validator.NotCyrillicValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Documented
@Constraint(validatedBy = NotCyrillicValidator.class)
public @interface ValidateNotCyrillic {

    String message() default "exception.not_cyrillic.wrong";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}