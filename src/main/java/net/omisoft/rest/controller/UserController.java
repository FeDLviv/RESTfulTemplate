package net.omisoft.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import net.omisoft.rest.configuration.annotation.CurrentUser;
import net.omisoft.rest.model.UserEntity;
import net.omisoft.rest.pojo.AuthResponse;
import net.omisoft.rest.pojo.PasswordRequest;
import net.omisoft.rest.service.user.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.Min;

import static net.omisoft.rest.ApplicationConstants.API_V1_BASE_PATH;

@RestController
@RequestMapping(API_V1_BASE_PATH + "users")
@Validated
@Api(tags = "1.USER", description = "Operations associated with user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @DeleteMapping(value = "{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "\uD83D\uDD10 Remove user by id")
    public void deleteUser(@ApiParam(value = "Id user", defaultValue = "1", required = true) @Min(1) @PathVariable long id,
                           @ApiIgnore @CurrentUser UserEntity currentUser) {
        userService.deleteById(id);
    }

    @PatchMapping(value = "password")
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "\uD83D\uDD11 Change password (current user)")
    public AuthResponse updatePassword(@Validated @RequestBody PasswordRequest data,
                                       @ApiIgnore @CurrentUser UserEntity currentUser) {
        return userService.updatePassword(currentUser, data);
    }

}