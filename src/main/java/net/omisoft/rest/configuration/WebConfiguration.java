package net.omisoft.rest.configuration;

import lombok.AllArgsConstructor;
import net.omisoft.rest.resolver.RemoteIpArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static net.omisoft.rest.ApplicationConstants.API_V1_BASE_PATH;

@Configuration
@AllArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

    private final RemoteIpArgumentResolver remoteIpArgumentResolver;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(API_V1_BASE_PATH + "**").allowedMethods("*");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(remoteIpArgumentResolver);
    }

}