package net.omisoft.rest.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.ResourceSupport;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomId extends ResourceSupport {

    @JsonProperty(value = "id")
    @ApiModelProperty(notes = "Id", required = true, position = 0)
    private long identifier;

}