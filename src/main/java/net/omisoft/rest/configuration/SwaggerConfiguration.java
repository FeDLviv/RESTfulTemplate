package net.omisoft.rest.configuration;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
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
import java.util.Objects;
import java.util.stream.Stream;

import static net.omisoft.rest.ApplicationConstants.LANGUAGE_HEADER;
import static net.omisoft.rest.ApplicationConstants.PROFILE_PROD;

@Configuration
@EnableSwagger2
@AllArgsConstructor
public class SwaggerConfiguration {

    private final Environment environment;

    @Bean
    public Docket docket() {
        ArrayList<Parameter> parameters = new ArrayList();
        parameters.add(
                new ParameterBuilder()
                        .name(LANGUAGE_HEADER)
                        .description(LANGUAGE_HEADER)
                        .modelRef(new ModelRef("string"))
                        .parameterType("header")
                        .required(true)
                        .defaultValue("en")
                        .build()
        );
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(Stream.of(environment.getActiveProfiles()).anyMatch(profile -> !Objects.equals(profile, PROFILE_PROD)))
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.any())
                .build().apiInfo(getApiInfo())
                .globalOperationParameters(parameters);
    }

    private ApiInfo getApiInfo() {
        return new ApiInfo(
                //TODO XXX
                "XXX API Documentation",
                //TODO XXX
                "Spring Boot RESTful API for XXX",
                "1.0",
                "",
                new Contact("OmiSoft", "http://www.omisoft.net", "omisoftnet@gmail.com"),
                null,
                null,
                new ArrayList<VendorExtension>());
    }

}

