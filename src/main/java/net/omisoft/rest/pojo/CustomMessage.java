package net.omisoft.rest.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomMessage {

    @ApiModelProperty(notes = "Message", required = true, position = 0)
    private String message;

}
