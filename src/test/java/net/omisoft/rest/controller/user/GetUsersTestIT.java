package net.omisoft.rest.controller.user;

import net.omisoft.rest.controller.BaseTestIT;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Date;

import static net.omisoft.rest.ApplicationConstants.AUTH_HEADER;
import static net.omisoft.rest.ApplicationConstants.TOKEN_PREFIX;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
INTEGRATION TEST
 */

public class GetUsersTestIT extends BaseTestIT {

    private static final String URL = URI + "users";

    @Test
    public void withSomeEmailValue() throws Exception {
        //prepare
        token = generateAndInsertToken(USER_ID_ADMIN, "ROLE_ADMIN", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        String emailValue = "fed.";
        //test + validate
        mvc.perform(
                get(URL + "?email=" + emailValue)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(EMAIL_EXISTS));
    }

    @Test
    public void withSomeEmailValueWrongMinLimit() throws Exception {
        //prepare
        token = generateAndInsertToken(USER_ID_ADMIN, "ROLE_ADMIN", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        String emailValue = "fe";
        //test + validate
        mvc.perform(
                get(URL + "?email=" + emailValue)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.property").value("email"));
    }

    @Test
    public void withRoleValueIsClient() throws Exception {
        //prepare
        token = generateAndInsertToken(USER_ID_ADMIN, "ROLE_ADMIN", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        String roleValue = "ROLE_CLIENT";
        //test + validate
        mvc.perform(
                get(URL + "?role=" + roleValue)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].role").value(roleValue));
    }

    @Test
    public void withIncorrectRoleValue() throws Exception {
        //prepare
        token = generateAndInsertToken(USER_ID_ADMIN, "ROLE_ADMIN", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        String roleValue = "vctOLE_WSAfsd";
        //test + validate
        mvc.perform(
                get(URL + "?role=" + roleValue)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.property", Matchers.startsWith("role")));
    }

    @Test
    public void withSomeEmailValueAndRoleValueIsClient() throws Exception {
        //prepare
        token = generateAndInsertToken(USER_ID_ADMIN, "ROLE_ADMIN", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        String emailValue = "fed.";
        String roleValue = "ROLE_CLIENT";
        //test + validate
        mvc.perform(
                get(URL + "?email=" + emailValue + "&role=" + roleValue)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Override
    public void expireToken() throws Exception {
        //prepare
        String token = generateToken(USER_ID_ADMIN, "ROLE_ADMIN", new Date());
        //test + validate
        mvc.perform(
                get(URL)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.token.expire")));
    }

    @Override
    public void wrongToken() throws Exception {
        //test + validate
        mvc.perform(
                get(URL)
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
                get(URL)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.token.not_exists")));
    }

    @Override
    public void notAuthorized() throws Exception {
        //test + validate
        mvc.perform(
                get(URL)
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
                get(URL)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Override
    public void authorizedClient() throws Exception {
        //prepare
        token = generateAndInsertToken(USER_ID_CLIENT, "ROLE_CLIENT", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        //test + validate
        mvc.perform(
                get(URL)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.auth.permission")));
    }

}