package net.omisoft.rest.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.omisoft.rest.dto.validator.annotation.ValidateNotCyrillic;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static net.omisoft.rest.ApplicationConstants.PASSWORD_MAX_LENGTH;
import static net.omisoft.rest.ApplicationConstants.PASSWORD_MIN_LENGTH;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateDto {

    @NotBlank
    @Size(max = 100)
    @Email
    @ValidateNotCyrillic
    @ApiModelProperty(notes = "Email", value = "fed.lviv@gmail.com", example = "fed.lviv@gmail.com", required = true, position = 0)
    private String email;

    @NotNull
    @ValidateNotCyrillic
    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    @ApiModelProperty(notes = "Password", value = "1234", example = "1234", required = true, position = 1)
    private String password;

}