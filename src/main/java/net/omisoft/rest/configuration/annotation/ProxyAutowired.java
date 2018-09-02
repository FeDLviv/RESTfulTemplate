package net.omisoft.rest.configuration.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//field not constructs argument (not final)
//from Spring 4.3 can use @Autowired
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ProxyAutowired {

}