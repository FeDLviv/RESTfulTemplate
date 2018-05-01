package net.omisoft.rest.configuration;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.omisoft.rest.ApplicationConstants.*;

@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
@AllArgsConstructor
public class SwaggerConfiguration {

    //TODO set icon
    private static final String[] AUTH_ICON = {
            "\u2000\u2000 - all",
            "\uD83D\uDD11 - ???",
            "\uD83D\uDD10 - ???"
    };

    private final Environment environment;

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(Stream.of(environment.getActiveProfiles()).anyMatch(profile -> !Objects.equals(profile, PROFILE_PROD)))
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.any())
                .build().apiInfo(getApiInfo())
                .globalOperationParameters(getParameters());
    }

    private ApiInfo getApiInfo() {
        return new ApiInfo(
                environment.getProperty("info.app.name")+" API Documentation",
                "Spring Boot RESTful API for " + environment.getProperty("info.app.name") +
                        "\n\rAccess:\n\r" +
                        Arrays.stream(AUTH_ICON).collect(Collectors.joining("\n\r")),
                environment.getProperty("info.app.version"),
                "",
                new Contact("OmiSoft", "http://www.omisoft.net", "omisoftnet@gmail.com"),
                null,
                null,
                new ArrayList<>());
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

}

