package net.omisoft.rest.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomId  {

    @ApiModelProperty(notes = "Id", required = true, position = 0)
    private long id;

}