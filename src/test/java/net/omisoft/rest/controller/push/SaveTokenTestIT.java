package net.omisoft.rest.controller.push;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.omisoft.rest.controller.BaseTestIT;
import net.omisoft.rest.dto.fcm.FCMTokenCreateDto;
import net.omisoft.rest.model.base.OS;
import org.apache.logging.log4j.util.Strings;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static net.omisoft.rest.ApplicationConstants.AUTH_HEADER;
import static net.omisoft.rest.ApplicationConstants.TOKEN_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;

/*
INTEGRATION TEST
 */

public class SaveTokenTestIT extends BaseTestIT {

    private static final String URL = URI + "push/token";

    @Test
    @Rollback
    public void tokenIsNull() throws Exception {
        incorrectValidationBody(OS.ANDROID.toString(), "Mobile", null);
    }

    @Test
    @Rollback
    public void tokenIsEmpty() throws Exception {
        incorrectValidationBody(OS.ANDROID.toString(), "Mobile", Strings.EMPTY);
    }

    @Test
    @Rollback
    public void deviceIsNull() throws Exception {
        incorrectValidationBody(OS.ANDROID.toString(), null, "sdsf");
    }

    @Test
    @Rollback
    public void deviceIsEmpty() throws Exception {
        incorrectValidationBody(OS.ANDROID.toString(), Strings.EMPTY, "sfdsfd");
    }

    @Test
    @Rollback
    public void osIsNull() throws Exception {
        incorrectValidationBody(null, "Mobile", "sdsf");
    }

    @Test
    @Rollback
    public void osWrong() throws Exception {
        //prepare
        List<String> list = Arrays.stream(OS.class.getEnumConstants())
                .map(Object::toString)
                .collect(Collectors.toList());
        token = generateAndInsertClientToken(USER_ID_CLIENT, new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        FCMTokenCreateDto body = FCMTokenCreateDto
                .builder()
                .os("dfio")
                .device("Mobile")
                .token("sfdsfd")
                .build();
        //test + validate
        mvc.perform(
                MockMvcRequestBuilders.post(URL)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(body))
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.property").value("device_os"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(message.getMessage("exception.enum.wrong", new Object[]{String.join(", ", list)})));
    }

    @Override
    public void expireToken() throws Exception {
        //prepare
        String token = generateAdminToken(USER_ID_ADMIN, new Date());
        FCMTokenCreateDto body = FCMTokenCreateDto
                .builder()
                .os(OS.IOS.toString())
                .device("PC")
                .token("kon9udjm")
                .build();
        //test + validate
        mvc.perform(
                MockMvcRequestBuilders.post(URL)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(body))
        )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(message.getMessage("exception.token.expire")));
    }

    @Override
    public void wrongToken() throws Exception {
        //prepare
        FCMTokenCreateDto body = FCMTokenCreateDto
                .builder()
                .os(OS.IOS.toString())
                .device("PC")
                .token("kon9udjm")
                .build();
        //test + validate
        mvc.perform(
                MockMvcRequestBuilders.post(URL)
                        .header(AUTH_HEADER, TOKEN_PREFIX + WRONG_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(body))
        )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(message.getMessage("exception.token.wrong")));
    }

    @Override
    public void tokenNotExists() throws Exception {
        //prepare
        String token = generateAdminToken(USER_ID_ADMIN, new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        FCMTokenCreateDto body = FCMTokenCreateDto
                .builder()
                .os(OS.IOS.toString())
                .device("PC")
                .token("kon9udjm")
                .build();
        //test + validate
        mvc.perform(
                MockMvcRequestBuilders.post(URL)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(body))
        )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(message.getMessage("exception.token.not_exists")));
    }

    @Override
    public void notAuthorized() throws Exception {
        //prepare
        FCMTokenCreateDto body = FCMTokenCreateDto
                .builder()
                .os(OS.IOS.toString())
                .device("PC")
                .token("kon9udjm")
                .build();
        //test + validate
        mvc.perform(
                MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(body))
        )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(message.getMessage("exception.auth.required")));
    }

    @Override
    public void authorizedAdmin() throws Exception {
        //prepare
        token = generateAndInsertAdminToken(USER_ID_ADMIN, new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        FCMTokenCreateDto body = FCMTokenCreateDto
                .builder()
                .os(OS.IOS.toString())
                .device("PC")
                .token("kon9udjm")
                .build();
        //test + validate
        mvc.perform(
                MockMvcRequestBuilders.post(URL)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(body))
        )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotHaveJsonPath());
    }

    @Override
    public void authorizedClient() throws Exception {
        //prepare
        token = generateAndInsertClientToken(USER_ID_CLIENT, new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        FCMTokenCreateDto body = FCMTokenCreateDto
                .builder()
                .os(OS.ANDROID.toString())
                .device("PC")
                .token("jo57udjf")
                .build();
        //test + validate
        mvc.perform(
                MockMvcRequestBuilders.post(URL)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(body))
        )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotHaveJsonPath());
    }

    private void incorrectValidationBody(String os, String device, String fcmToken) throws Exception {
        //prepare
        token = generateAndInsertClientToken(USER_ID_CLIENT, new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        FCMTokenCreateDto body = FCMTokenCreateDto
                .builder()
                .os(os)
                .device(device)
                .token(fcmToken)
                .build();
        //test + validate
        assertThat(validator.validate(body).isEmpty()).isFalse();
        mvc.perform(
                MockMvcRequestBuilders.post(URL)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(body))
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }

}