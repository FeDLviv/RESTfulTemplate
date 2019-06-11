package net.omisoft.rest.dto.validator.annotation;

import net.omisoft.rest.dto.validator.EnumValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Documented
@Constraint(validatedBy = EnumValidator.class)
@Repeatable(ValidateEnum.List.class)
public @interface ValidateEnum {

    String message() default "exception.enum.wrong";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<?> enumeration();

    String[] exclude() default {};

    String[] include() default {};

    boolean ignoreCase() default false;

    @Retention(RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Documented
    @interface List {
        ValidateEnum[] value();
    }

}
