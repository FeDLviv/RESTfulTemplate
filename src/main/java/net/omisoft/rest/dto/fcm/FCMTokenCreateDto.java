package net.omisoft.rest.dto.fcm;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import net.omisoft.rest.dto.validator.annotation.ValidateEnum;
import net.omisoft.rest.model.base.OS;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class FCMTokenCreateDto {

    @NotBlank
    @Size(max = 255)
    @ApiModelProperty(notes = "Token", value = "fQVo_SbUASQ:APA91bHtV6XkSgW5jHIz6yXaRfddkWqKOIORzg13KJAXnb24wxBIcwQf73Ni9ZjdK5szf01PHkuIATdsHj-Sm5g6twqqYzqix-sPrPRJqo0ulC-HcVrByRaxGpV5Ey1Ik8CXeEg4Kpj1", example = "fQVo_SbUASQ:APA91bHtV6XkSgW5jHIz6yXaRfddkWqKOIORzg13KJAXnb24wxBIcwQf73Ni9ZjdK5szf01PHkuIATdsHj-Sm5g6twqqYzqix-sPrPRJqo0ulC-HcVrByRaxGpV5Ey1Ik8CXeEg4Kpj1", required = true, position = 0)
    private String token;

    @NotBlank
    @Size(max = 255)
    @JsonProperty(value = "device_number")
    @ApiModelProperty(notes = "Number device", value = "pc-my-unique", example = "pc-my-unique", required = true, position = 1)
    private String device;

    @NotBlank
    @ValidateEnum(enumeration = OS.class)
    @JsonProperty(value = "device_os")
    @ApiModelProperty(notes = "OS device", value = "ANDROID", example = "ANDROID", required = true, position = 2)
    private String os;

}