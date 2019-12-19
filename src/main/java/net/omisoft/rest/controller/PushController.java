package net.omisoft.rest.controller;

import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import net.omisoft.rest.configuration.annotation.CurrentUser;
import net.omisoft.rest.dto.fcm.FCMTokenCreateDto;
import net.omisoft.rest.model.UserEntity;
import net.omisoft.rest.pojo.CustomMessage;
import net.omisoft.rest.service.push.PushService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import static net.omisoft.rest.ApplicationConstants.API_V1_BASE_PATH;

@RestController
@RequestMapping(value = API_V1_BASE_PATH + "push", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Api(tags = "3.PUSH")
@AllArgsConstructor
public class PushController {

    private final PushService pushService;

    @PostMapping(value = "token")
    @ResponseStatus(value = HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "\uD83D\uDD11 Add or update push token (current user)")
    @ApiResponses({
            @ApiResponse(code = 400, message = "Bad request", response = CustomMessage.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = CustomMessage.class)
    })
    public void saveToken(@ApiParam(value = "FCM token and metadata") @Validated @RequestBody FCMTokenCreateDto data,
                          @ApiIgnore @CurrentUser UserEntity currentUser) {
        pushService.saveToken(data, currentUser);
    }

}