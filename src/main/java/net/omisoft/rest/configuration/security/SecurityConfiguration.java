package net.omisoft.rest.configuration.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Objects;
import java.util.stream.Stream;

import static net.omisoft.rest.ApplicationConstants.PROFILE_PROD;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, jsr250Enabled = true)
@AllArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JWTAuthenticationFilter jwtFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final Environment environment;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //TODO show H2
        http.headers().frameOptions().disable();
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler);
        //TODO change role
        if (Stream.of(environment.getActiveProfiles()).anyMatch(profile -> Objects.equals(profile, PROFILE_PROD))) {
            http.authorizeRequests()
                    .antMatchers(environment.getProperty("management.endpoints.web.base-path") + "/**")
                    .hasAnyRole("ADMIN");
        }
    }

}