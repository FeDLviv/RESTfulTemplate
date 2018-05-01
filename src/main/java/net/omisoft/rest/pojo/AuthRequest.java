package net.omisoft.rest.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequest {

    @NotBlank
    @Email
    @ApiModelProperty(notes = "Email", value = "fed.lviv@gmail.com", example = "fed.lviv@gmail.com")
    private String email;

    @NotBlank
    @ApiModelProperty(notes = "Password", value = "1111", example = "1111")
    private String password;

}
