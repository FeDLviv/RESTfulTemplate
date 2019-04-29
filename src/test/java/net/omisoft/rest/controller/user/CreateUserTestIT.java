package net.omisoft.rest.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import liquibase.util.StringUtils;
import net.omisoft.rest.controller.BaseTestIT;
import net.omisoft.rest.dto.user.UserCreateDto;
import org.apache.logging.log4j.util.Strings;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.Date;
import java.util.Random;
import java.util.stream.Collectors;

import static net.omisoft.rest.ApplicationConstants.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
INTEGRATION TEST
 */

public class CreateUserTestIT extends BaseTestIT {

    private static final String URL = URI + "users";

    @Test
    public void emailIsExists() throws Exception {
        //prepare
        UserCreateDto body = UserCreateDto
                .builder()
                .email(EMAIL_EXISTS)
                .password(PASSWORD_EXISTS)
                .build();
        token = generateAndInsertToken(USER_ID_ADMIN, "ROLE_ADMIN", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        //test + validate
        mvc.perform(
                post(URL)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(body))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.property").doesNotExist())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.email.exists")));
    }

    @Test
    public void emailIsNull() throws Exception {
        incorrectValidationBody(null, PASSWORD_EXISTS, "email");
    }

    @Test
    public void emailIsEmpty() throws Exception {
        incorrectValidationBody(Strings.EMPTY, PASSWORD_EXISTS, "email");
    }

    @Test
    public void emailWithCyrillic() throws Exception {
        incorrectValidationBody("feд.lviv@gmail.com", PASSWORD_EXISTS, "email");
    }

    @Test
    public void emailIncorrect() throws Exception {
        incorrectValidationBody("sdfs", PASSWORD_EXISTS, "email");
    }

    @Test
    public void emailWrongMaxLimit() throws Exception {
        incorrectValidationBody(EMAIL_EXISTS + StringUtils.repeat("s", 100), "1111", "email");
    }

    @Test
    public void passwordIsNull() throws Exception {
        incorrectValidationBody(EMAIL_NOT_EXISTS, null, "password");
    }

    @Test
    public void passwordWithCyrillic() throws Exception {
        incorrectValidationBody(EMAIL_NOT_EXISTS, "sssи", "password");
    }

    @Test
    public void passwordWrongMaxLimit() throws Exception {
        String password = "s" + new Random()
                .ints(0, 9)
                .limit(PASSWORD_MAX_LENGTH)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining(""));
        incorrectValidationBody(EMAIL_NOT_EXISTS, password, "password");
    }

    @Test
    public void passwordWrongMinLimit() throws Exception {
        String password = "s" + new Random()
                .ints(0, 9)
                .limit(PASSWORD_MIN_LENGTH - 2)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining(""));
        incorrectValidationBody(EMAIL_NOT_EXISTS, password, "password");
    }

    @Override
    public void expireToken() throws Exception {
        //prepare
        token = generateToken(USER_ID_ADMIN, "ROLE_ADMIN", new Date());
        UserCreateDto body = UserCreateDto
                .builder()
                .email(EMAIL_NOT_EXISTS)
                .password(PASSWORD_EXISTS)
                .build();
        //test + validate
        mvc.perform(
                post(URL)
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
        UserCreateDto body = UserCreateDto
                .builder()
                .email(EMAIL_NOT_EXISTS)
                .password(PASSWORD_EXISTS)
                .build();
        //test + validate
        mvc.perform(
                post(URL)
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
        token = generateToken(USER_ID_ADMIN, "ROLE_ADMIN", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        UserCreateDto body = UserCreateDto
                .builder()
                .email(EMAIL_NOT_EXISTS)
                .password(PASSWORD_EXISTS)
                .build();
        //test + validate
        mvc.perform(
                post(URL)
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
        UserCreateDto body = UserCreateDto
                .builder()
                .email(EMAIL_NOT_EXISTS)
                .password(PASSWORD_EXISTS)
                .build();
        //test + validate
        mvc.perform(
                post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(body))
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$._links").isMap())
                .andExpect(jsonPath("$._links.*", hasSize(2)));
    }

    @Override
    public void authorizedAdmin() throws Exception {
        //prepare
        token = generateAndInsertToken(USER_ID_ADMIN, "ROLE_ADMIN", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        UserCreateDto body = UserCreateDto
                .builder()
                .email(EMAIL_NOT_EXISTS)
                .password(PASSWORD_EXISTS)
                .build();
        //test + validate
        mvc.perform(
                post(URL)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(body))
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$._links").isMap())
                .andExpect(jsonPath("$._links.*", hasSize(2)));
    }

    @Override
    public void authorizedClient() throws Exception {
        //prepare
        token = generateAndInsertToken(USER_ID_CLIENT, "ROLE_CLIENT", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        UserCreateDto body = UserCreateDto
                .builder()
                .email(EMAIL_NOT_EXISTS)
                .password(PASSWORD_EXISTS)
                .build();
        //test + validate
        mvc.perform(
                post(URL)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(body))
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$._links").isMap())
                .andExpect(jsonPath("$._links.*", hasSize(2)));
    }

    private void incorrectValidationBody(String email, String password, String propertyName) throws Exception {
        //prepare
        UserCreateDto body = UserCreateDto
                .builder()
                .email(email)
                .password(password)
                .build();
        token = generateAndInsertToken(USER_ID_ADMIN, "ROLE_ADMIN", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        //test + validate
        mvc.perform(
                post(URL)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(body))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.property").value(propertyName))
                .andExpect(jsonPath("$.message").exists());
    }

}