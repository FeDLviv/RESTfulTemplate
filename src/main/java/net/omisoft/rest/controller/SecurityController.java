package net.omisoft.rest.controller;

import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import net.omisoft.rest.configuration.PropertiesConfiguration;
import net.omisoft.rest.pojo.AuthRequest;
import net.omisoft.rest.pojo.AuthResponse;
import net.omisoft.rest.pojo.CustomMessage;
import net.omisoft.rest.service.security.JWTService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static net.omisoft.rest.ApplicationConstants.API_V1_BASE_PATH;

@RestController
@RequestMapping(value = API_V1_BASE_PATH + "security", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Validated
@Api(tags = "1.SECURITY", description = "Operations associated with authentication")
@AllArgsConstructor
public class SecurityController {

    private final JWTService service;
    private final PropertiesConfiguration propertiesConfiguration;

    @PostMapping(value = "auth")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiOperation(value = "Authentication (get token)")
    @ApiResponses({
            @ApiResponse(code = 400, message = "Bad request", response = CustomMessage.class)
    })
    public AuthResponse login(@ApiParam(value = "User credentials") @Validated @RequestBody AuthRequest credential) {
        return AuthResponse.builder()
                .token(service.getToken(credential))
                .duration(propertiesConfiguration.getToken().getDuration())
                .build();
    }

}