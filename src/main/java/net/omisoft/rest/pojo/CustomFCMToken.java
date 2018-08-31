package net.omisoft.rest.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.omisoft.rest.model.base.OS;

@Data
@AllArgsConstructor
public class CustomFCMToken {

    private String token;

    private OS os;

}