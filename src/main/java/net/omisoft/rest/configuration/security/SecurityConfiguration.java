package net.omisoft.rest.configuration.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.stream.Stream;

import static net.omisoft.rest.ApplicationConstants.PROFILE_PROD;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JWTAuthenticationFilter jwtFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final Environment environment;
    private final String accessExpresion;

    public SecurityConfiguration(JWTAuthenticationFilter jwtFilter,
                                 CustomAuthenticationEntryPoint authenticationEntryPoint,
                                 CustomAccessDeniedHandler accessDeniedHandler,
                                 Environment environment) throws MalformedURLException, UnknownHostException {
        this.jwtFilter = jwtFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.environment = environment;
        this.accessExpresion = generateAccessExpression(environment.getProperty("spring.boot.admin.client.url"));
    }

    private String generateAccessExpression(String url) throws MalformedURLException, UnknownHostException {
        //TODO change role
        if (url != null) {
            InetAddress address = InetAddress.getByName(new URL(url).getHost());
            String ip = address.getHostAddress();
            return "hasRole('ADMIN') or hasIpAddress('" + ip + "')";
        } else {
            return "hasRole('ADMIN')";
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //TODO show H2
        http.headers().frameOptions().disable();
        http.csrf().disable();
        http.cors();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler);
        if (Stream.of(environment.getActiveProfiles()).anyMatch(profile -> Objects.equals(profile, PROFILE_PROD))) {
            http.authorizeRequests()
                    .antMatchers(environment.getProperty("management.endpoints.web.base-path") + "/**")
                    .access(accessExpresion);
        }
    }

}