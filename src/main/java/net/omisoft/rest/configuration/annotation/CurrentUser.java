package net.omisoft.rest.configuration.annotation;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.PARAMETER})
@Documented
@AuthenticationPrincipal
public @interface CurrentUser {

}
