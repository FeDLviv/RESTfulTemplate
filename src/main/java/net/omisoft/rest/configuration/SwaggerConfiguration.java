package net.omisoft.rest.configuration;

import com.fasterxml.classmate.TypeResolver;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpMethod;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static net.omisoft.rest.ApplicationConstants.*;

//http://www.fileformat.info/info/unicode/char/search.htm
@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
@AllArgsConstructor
public class SwaggerConfiguration {

    //TODO set icon
    private static final String[] AUTH_ICON = {
            "* `all`",
            "* \uD83D\uDD11 - only `client` or `admin`",
            "* \uD83D\uDD10 - only `admin`"
    };

    private final Environment environment;
    private final TypeResolver typeResolver;

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(Stream.of(environment.getActiveProfiles()).anyMatch(profile -> !Objects.equals(profile, PROFILE_PROD)))
                .apiInfo(getApiInfo())
                .globalOperationParameters(getParameters())
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.any())
                .build()
                .alternateTypeRules(
                        AlternateTypeRules.newRule(typeResolver.resolve(List.class, Link.class),
                                typeResolver.resolve(Map.class, String.class, LinkAlternative.class))
                );
    }

    @Bean
    UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .displayRequestDuration(true)
                .filter(true)
                .operationsSorter(OperationsSorter.METHOD)
                .build();
    }

    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .title(environment.getProperty("info.app.name") + " API Documentation")
                .description("Spring Boot RESTful API for " + environment.getProperty("info.app.name") +
                        "\n\n**Access:**\n" +
                        String.join("\n", AUTH_ICON))
                .version(environment.getProperty("info.app.version"))
                .contact(new Contact("OmiSoft", "http://www.omisoft.net", "omisoftnet@gmail.com"))
                .build();
    }

    private List<Parameter> getParameters() {
        ArrayList<Parameter> parameters = new ArrayList<>();
        parameters.add(
                new ParameterBuilder()
                        .name(LANGUAGE_HEADER)
                        .description(LANGUAGE_HEADER)
                        .modelRef(new ModelRef("string"))
                        .parameterType("header")
                        .required(false)
                        .defaultValue("en")
                        .build()
        );
        parameters.add(
                new ParameterBuilder()
                        .name(AUTH_HEADER)
                        .description(AUTH_HEADER)
                        .modelRef(new ModelRef("string"))
                        .parameterType("header")
                        .required(false)
                        .defaultValue(TOKEN_PREFIX)
                        .build()
        );

        return parameters;
    }

    @Data
    private class LinkAlternative {

        @ApiModelProperty(notes = "Hyper reference", value = "http://example/api/v1/users", example = "http://example/api/v1/users", required = true, position = 0)
        private String href;

        @ApiModelProperty(notes = "HTTP Method", value = "POST", example = "POST", required = true, position = 1)
        private HttpMethod type;

    }

}