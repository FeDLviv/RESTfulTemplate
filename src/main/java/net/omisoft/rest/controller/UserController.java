package net.omisoft.rest.controller;

import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import net.omisoft.rest.configuration.annotation.CurrentUser;
import net.omisoft.rest.dto.user.UserBasicDto;
import net.omisoft.rest.dto.user.UserCreateDto;
import net.omisoft.rest.dto.validator.annotation.ValidateEnum;
import net.omisoft.rest.mapper.UserMapper;
import net.omisoft.rest.model.UserEntity;
import net.omisoft.rest.model.base.UserRole;
import net.omisoft.rest.pojo.AuthResponse;
import net.omisoft.rest.pojo.CustomId;
import net.omisoft.rest.pojo.CustomMessage;
import net.omisoft.rest.pojo.PasswordRequest;
import net.omisoft.rest.repository.specification.UserEmailAndRolesSpecification;
import net.omisoft.rest.service.user.UserService;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;

import static net.omisoft.rest.ApplicationConstants.API_V1_BASE_PATH;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping(value = API_V1_BASE_PATH + "users", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Api(tags = "2.USER")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping(value = "", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiOperation(value = "Create user")
    @ApiResponses({
            @ApiResponse(code = 400, message = "Bad request", response = CustomMessage.class)
    })

    public CustomId addUser(@ApiParam(value = "User data", required = true) @Validated @RequestBody UserCreateDto data,
                            @ApiIgnore @CurrentUser UserEntity currentUser) throws NoSuchMethodException {
        CustomId id = new CustomId(userService.create(userMapper.convert(data)).getId());
        id.add(linkTo(UserController.class.getMethod("deleteUser", long.class, UserEntity.class), id.getIdentifier(), currentUser)
                .withRel("remove")
                .withType("DELETE"));
        id.add(linkTo(UserController.class.getMethod("updatePassword", PasswordRequest.class, UserEntity.class), null, currentUser)
                .withRel("change_password")
                .withType("PATCH"));
        return id;
    }

    @DeleteMapping(value = "{id}")
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "\uD83D\uDD11 Remove user by id")
    @ApiResponses({
            @ApiResponse(code = 400, message = "Bad request", response = CustomMessage.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = CustomMessage.class),
            @ApiResponse(code = 403, message = "Forbidden", response = CustomMessage.class),
            @ApiResponse(code = 404, message = "Not found", response = CustomMessage.class)
    })
    public void deleteUser(@ApiParam(value = "Id user", defaultValue = "1", required = true) @Positive @PathVariable long id,
                           @ApiIgnore @CurrentUser UserEntity currentUser) {
        userService.deleteById(id, currentUser);
    }

    @GetMapping(value = "")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "\uD83D\uDD10 Get users, filter by email (like) and roles (in)")
    @ApiResponses({
            @ApiResponse(code = 400, message = "Bad request", response = CustomMessage.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = CustomMessage.class),
            @ApiResponse(code = 403, message = "Forbidden", response = CustomMessage.class)
    })
    public List<UserBasicDto> getUsers(@ApiParam(value = "Email", required = false) @RequestParam(value = "email", required = false) @Size(min = 3) String email,
                                       @ApiParam(value = "Roles", required = false) @RequestParam(value = "role", required = false) List<@ValidateEnum(enumeration = UserRole.class) String> role,
                                       @ApiIgnore UserEmailAndRolesSpecification userSpecification,
                                       @ApiIgnore @CurrentUser UserEntity currentUser) {
        return userMapper.convert(userService.getUsers(userSpecification));
    }

    @PatchMapping(value = "password")
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "\uD83D\uDD11 Change password (current user)")
    @ApiResponses({
            @ApiResponse(code = 400, message = "Bad request", response = CustomMessage.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = CustomMessage.class)
    })
    public AuthResponse updatePassword(@ApiParam(value = "Old and new passwords") @Validated @RequestBody PasswordRequest data,
                                       @ApiIgnore @CurrentUser UserEntity currentUser) {
        return userService.updatePassword(currentUser, data);
    }

}