package net.omisoft.rest.model.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.omisoft.rest.model.base.OS;

@Data
@AllArgsConstructor
public class FCMTokenProjection {

    private String token;

    private OS os;

}