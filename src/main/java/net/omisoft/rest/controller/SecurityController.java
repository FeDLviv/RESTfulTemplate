package net.omisoft.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import net.omisoft.rest.configuration.PropertiesConfiguration;
import net.omisoft.rest.pojo.AuthRequest;
import net.omisoft.rest.pojo.AuthResponse;
import net.omisoft.rest.service.security.JWTService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static net.omisoft.rest.ApplicationConstants.API_V1_BASE_PATH;

@RestController
@RequestMapping(API_V1_BASE_PATH + "security")
@Validated
@Api(tags = "1.SECURITY", description = "Operations associated with authentication")
@AllArgsConstructor
public class SecurityController {

    private final JWTService service;
    private final PropertiesConfiguration propertiesConfiguration;

    @PostMapping(value = "auth")
    @ApiOperation(value = "Authentification (get token)")
    public AuthResponse login(@Validated @RequestBody AuthRequest credential) {
        return AuthResponse.builder()
                .token(service.getToken(credential))
                .duration(propertiesConfiguration.getToken().getDuration())
                .build();
    }

}
