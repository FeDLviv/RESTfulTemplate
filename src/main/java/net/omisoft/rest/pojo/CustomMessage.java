package net.omisoft.rest.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class CustomMessage {

    @ApiModelProperty(notes = "Message", required = true, position = 0)
    @NonNull
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(notes = "Property", required = false, position = 1)
    private String property;

}
