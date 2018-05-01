package net.omisoft.rest.configuration.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled=true, jsr250Enabled=true)
@AllArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final Environment environment;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //TODO show H2
        http.headers().frameOptions().disable();
        http.csrf().disable();
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
        //TODO change role
        http.authorizeRequests()
                .antMatchers(environment.getProperty("management.endpoints.web.base-path") + "/**")
                .hasAnyRole("ADMIN");

    }

}

