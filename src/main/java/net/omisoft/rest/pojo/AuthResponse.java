package net.omisoft.rest.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    @ApiModelProperty(notes = "Token", value = "eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiIxIiwiZXhwIjoxNTIwNzYyMzk4fQ.B_aehV-cinA74VqH0WBsODdBJkgtfkvRg_k1VOUgoMhkyl_61mm-GCKP4mNlUCnd52zVcq_GY4c5FNWRE4-ifA", example = "eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiIxIiwiZXhwIjoxNTIwNzYyMzk4fQ.B_aehV-cinA74VqH0WBsODdBJkgtfkvRg_k1VOUgoMhkyl_61mm-GCKP4mNlUCnd52zVcq_GY4c5FNWRE4-ifA")
    private String token;

    @ApiModelProperty(notes = "Duration", value = "604800000", example = "604800000")
    private long duration;

}
