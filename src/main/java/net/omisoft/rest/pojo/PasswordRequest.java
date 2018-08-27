package net.omisoft.rest.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static net.omisoft.rest.ApplicationConstants.PASSWORD_MAX_LENGTH;
import static net.omisoft.rest.ApplicationConstants.PASSWORD_MIN_LENGTH;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordRequest {

    @NotBlank
    @ApiModelProperty(notes = "Old password", value = "1111", example = "1111", required = true, position = 0)
    @JsonProperty(value = "old_password")
    private String oldPassword;

    @NotBlank
    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    @ApiModelProperty(notes = "New password", value = "1111", example = "1111", required = true, position = 1)
    @JsonProperty(value = "new_password")
    private String newPassword;

}