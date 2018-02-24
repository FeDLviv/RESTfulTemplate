package net.omisoft.rest.configuration;

import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket docket() {
        ArrayList<Parameter> parameters = new ArrayList();
        parameters.add(
                new ParameterBuilder()
                        .name("Accept-Language")
                        .description("Accept-Language")
                        .modelRef(new ModelRef("string"))
                        .parameterType("header")
                        .required(true)
                        .defaultValue("en")
                        .build()
        );
        return new Docket(DocumentationType.SWAGGER_2)
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

