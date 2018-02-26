package net.omisoft.rest.configuration.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static net.omisoft.rest.ApplicationConstants.API_V1_BASE_PATH;

@Configuration
@AllArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final RestAuthenticationEntryPoint authenticationEntryPoint;

    private static final String[] SWAGGER_PATH = {
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/v2/api-docs"
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //TODO show H2
        http.headers().frameOptions().disable();
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(SWAGGER_PATH).permitAll()
                //TODO permission only ADMIN
                .antMatchers(API_V1_BASE_PATH + "**").hasRole("ADMIN")
                .anyRequest().permitAll()
                .and()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
    }

}

