package net.omisoft.rest.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBasicDto {

    @ApiModelProperty(notes = "Id", value = "1", example = "1", position = 0, required = true)
    private long id;

    @ApiModelProperty(notes = "Email", value = "fed.lviv@gmail.com", example = "fed.lviv@gmail.com", position = 1, required = true)
    private String email;

    @ApiModelProperty(notes = "Role", value = "ROLE_ADMIN", example = "ROLE_ADMIN", position = 2, required = true)
    private String role;

}