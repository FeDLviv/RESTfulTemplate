package net.omisoft.rest.controllers.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.omisoft.rest.controllers.BaseTestIT;
import net.omisoft.rest.pojo.PasswordRequest;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;
import java.util.Random;
import java.util.stream.Collectors;

import static net.omisoft.rest.ApplicationConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
INTEGRATION TEST
 */

public class UpdatePasswordTestIT extends BaseTestIT {

    private static final String URL = URI + "users/password";

    @Test
    @Rollback
    public void oldPasswordIsNull() throws Exception {
        incorrectValidationRequiredBody(null, "1111");
    }

    @Test
    @Rollback
    public void newPasswordIsNull() throws Exception {
        incorrectValidationRequiredBody("1111", null);
    }

    @Test
    @Rollback
    public void newPasswordWrongMaxLimit() throws Exception {
        incorrectValidationRequiredBody(
                "1111",
                new Random()
                        .ints(0, 9)
                        .limit(PASSWORD_MAX_LENGTH + 1)
                        .mapToObj(Integer::toString)
                        .collect(Collectors.joining("")));
    }

    @Test
    @Rollback
    public void newPasswordWrongMinLimit() throws Exception {
        incorrectValidationRequiredBody(
                "1111",
                new Random()
                        .ints(0, 9)
                        .limit(PASSWORD_MIN_LENGTH - 1)
                        .mapToObj(Integer::toString)
                        .collect(Collectors.joining("")));
    }


    @Test
    @Rollback
    public void wrongOldPassword() throws Exception {
        //prepare
        String token = generateAndInsertToken(USER_ID_CLIENT, "ROLE_CLIENT", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        PasswordRequest body = PasswordRequest
                .builder()
                .oldPassword("11111")
                .newPassword("2222")
                .build();
        //test + validate
        mvc.perform(
                patch(URL)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(body))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.old_password.wrong")));
    }


    @Override
    public void expireToken() throws Exception {
        //prepare
        String token = generateToken(USER_ID_ADMIN, "ROLE_ADMIN", new Date());
        PasswordRequest body = PasswordRequest
                .builder()
                .oldPassword("1111")
                .newPassword("2222")
                .build();
        //test + validate
        mvc.perform(
                patch(URL)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(body))
        )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.token.expire")));
    }

    @Override
    public void wrongToken() throws Exception {
        //prepare
        PasswordRequest body = PasswordRequest
                .builder()
                .oldPassword("1111")
                .newPassword("2222")
                .build();
        //test + validate
        mvc.perform(
                patch(URL)
                        .header(AUTH_HEADER, TOKEN_PREFIX + WRONG_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(body))
        )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.token.wrong")));
    }

    @Override
    public void tokenNotExists() throws Exception {
        //prepare
        String token = generateToken(USER_ID_ADMIN, "ROLE_ADMIN", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        PasswordRequest body = PasswordRequest
                .builder()
                .oldPassword("1111")
                .newPassword("2222")
                .build();
        //test + validate
        mvc.perform(
                patch(URL)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(body))
        )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.token.not_exists")));
    }

    @Override
    public void notAuthorized() throws Exception {
        //prepare
        PasswordRequest body = PasswordRequest
                .builder()
                .oldPassword("1111")
                .newPassword("2222")
                .build();
        //test + validate
        mvc.perform(
                patch(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(body))
        )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.auth.required")));
    }

    @Override
    public void authorizedAdmin() throws Exception {
        //prepare
        String token = generateAndInsertToken(USER_ID_ADMIN, "ROLE_ADMIN", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        PasswordRequest body = PasswordRequest
                .builder()
                .oldPassword("1111")
                .newPassword("2222")
                .build();
        //test + validate
        mvc.perform(
                patch(URL)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(body))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.duration").isNumber());
    }

    @Override
    public void authorizedClient() throws Exception {
        //prepare
        String oldPassword = "1111";
        String newPassword = "2222";
        token = generateAndInsertToken(USER_ID_CLIENT, "ROLE_CLIENT", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        PasswordRequest body = PasswordRequest
                .builder()
                .oldPassword(oldPassword)
                .newPassword(newPassword)
                .build();
        //test + validate
        MvcResult result = mvc.perform(
                patch(URL)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(body))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.duration").isNumber())
                .andReturn();
        //test + validate (with old token)
        mvc.perform(
                patch(URL)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(body))
        )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.token.not_exists")));
        //prepare
        String newToken = new ObjectMapper().readTree(result.getResponse().getContentAsString()).path("token").asText();
        body.setOldPassword(newPassword);
        //test + validate (with new token)
        mvc.perform(
                patch(URL)
                        .header(AUTH_HEADER, TOKEN_PREFIX + newToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(body))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.duration").isNumber());
    }

    private void incorrectValidationRequiredBody(String oldPassword, String newPassword) throws Exception {
        //prepare
        token = generateAndInsertToken(USER_ID_CLIENT, "ROLE_CLIENT", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        PasswordRequest body = PasswordRequest
                .builder()
                .oldPassword(oldPassword)
                .newPassword(newPassword)
                .build();
        //test + validate
        assertThat(validator.validate(body).isEmpty()).isFalse();
        mvc.perform(
                patch(URL)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(body))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

}