package net.omisoft.rest.controller.user;

import net.omisoft.rest.controller.BaseTestIT;
import net.omisoft.rest.pojo.CustomMessage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;

import java.util.Date;

import static net.omisoft.rest.ApplicationConstants.AUTH_HEADER;
import static net.omisoft.rest.ApplicationConstants.TOKEN_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
INTEGRATION TEST
 */

public class DeleteUserTestIT extends BaseTestIT {

    private static final String URL = URI + "users/%d";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Rollback
    public void clientRemoveYourself() throws Exception {
        //prepare
        String clientToken = generateAndInsertToken(USER_ID_CLIENT, "ROLE_CLIENT", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        //test + validate (delete client)
        mvc.perform(
                delete(String.format(URL, USER_ID_CLIENT))
                        .header(AUTH_HEADER, TOKEN_PREFIX + clientToken)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotHaveJsonPath());
        //test + validate (after delete client)
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTH_HEADER, TOKEN_PREFIX + clientToken);
        ResponseEntity<CustomMessage> response = restTemplate.exchange(String.format(URL, USER_ID_ADMIN), HttpMethod.DELETE, new HttpEntity<>(headers), CustomMessage.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getMessage()).isNotEmpty();
    }


    @Test
    @Rollback
    public void incorrectUserId() throws Exception {
        //prepare
        token = generateAndInsertToken(USER_ID_ADMIN, "ROLE_ADMIN", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        //test + validate
        mvc.perform(
                delete(String.format(URL, -1))
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.property").value("id"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @Rollback
    public void userNotExists() throws Exception {
        //prepare
        token = generateAndInsertToken(USER_ID_ADMIN, "ROLE_ADMIN", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        //test + validate
        mvc.perform(
                delete(String.format(URL, USER_ID_NOT_EXISTS))
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.user.not_exists")));
    }


    @Override
    public void expireToken() throws Exception {
        //prepare
        String token = generateToken(USER_ID_ADMIN, "ROLE_ADMIN", new Date());
        //test + validate
        mvc.perform(
                delete(String.format(URL, USER_ID_CLIENT))
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.token.expire")));

    }

    @Override
    public void wrongToken() throws Exception {
        //test + validate
        mvc.perform(
                delete(String.format(URL, USER_ID_CLIENT))
                        .header(AUTH_HEADER, TOKEN_PREFIX + WRONG_TOKEN)
        )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.token.wrong")));

    }

    @Override
    public void tokenNotExists() throws Exception {
        //prepare
        String token = generateToken(USER_ID_ADMIN, "ROLE_ADMIN", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        //test + validate
        mvc.perform(
                delete(String.format(URL, USER_ID_CLIENT))
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.token.not_exists")));
    }

    @Override
    public void notAuthorized() throws Exception {
        //test + validate
        mvc.perform(
                delete(String.format(URL, USER_ID_CLIENT))
        )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.auth.required")));
    }

    @Override
    public void authorizedAdmin() throws Exception {
        //prepare
        token = generateAndInsertToken(USER_ID_ADMIN, "ROLE_ADMIN", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        //test + validate (before delete client)
        mvc.perform(
                delete(String.format(URL, USER_ID_CLIENT))
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotHaveJsonPath());
    }

    @Override
    public void authorizedClient() throws Exception {
        //prepare
        token = generateAndInsertToken(USER_ID_CLIENT, "ROLE_CLIENT", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        //test + validate
        mvc.perform(
                delete(String.format(URL, USER_ID_ADMIN))
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.auth.permission")));

    }

}