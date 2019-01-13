package net.omisoft.rest.controller.payment;

import net.omisoft.rest.controller.BaseTestIT;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.Date;

import static net.omisoft.rest.ApplicationConstants.AUTH_HEADER;
import static net.omisoft.rest.ApplicationConstants.TOKEN_PREFIX;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
INTEGRATION TEST
 */

public class PendingTestIT extends BaseTestIT {

    private static final String URL = "/payment/pending/%s";

    @Test
    public void uuidIsEmpty() throws Exception {
        //test + validate
        mvc.perform(
                post(URL.replaceAll("%s", ""))
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotHaveJsonPath());
    }

    @Test
    public void uuidIsNotExists() throws Exception {
        //test + validate
        mvc.perform(
                post(String.format(URL, PAYMENT_UUID_NOT_EXISTS))
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(HttpStatus.NOT_FOUND.getReasonPhrase()));
    }

    @Override
    public void expireToken() throws Exception {
        //prepare
        String token = generateToken(USER_ID_ADMIN, "ROLE_ADMIN", new Date());
        //test + validate
        mvc.perform(
                post(String.format(URL, PAYMENT_UUID_EXISTS))
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.token.expire")));
    }

    @Override
    public void wrongToken() throws Exception {
        //test + validate
        mvc.perform(
                post(String.format(URL, PAYMENT_UUID_EXISTS))
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
                post(String.format(URL, PAYMENT_UUID_EXISTS))
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.token.not_exists")));
    }

    @Override
    public void notAuthorized() throws Exception {
        //test + validate
        mvc.perform(
                post(String.format(URL, PAYMENT_UUID_EXISTS)))
                .andExpect(status().isOk())
                .andExpect(view().name("result"))
                .andExpect(model().attribute("result", message.getMessage("payment.pending")));
    }

    @Override
    public void authorizedAdmin() throws Exception {
        //prepare
        token = generateAndInsertToken(USER_ID_ADMIN, "ROLE_ADMIN", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        //test + validate
        mvc.perform(
                post(String.format(URL, PAYMENT_UUID_EXISTS))
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("result"))
                .andExpect(model().attribute("result", message.getMessage("payment.pending")));
    }

    @Override
    public void authorizedClient() throws Exception {
        //prepare
        token = generateAndInsertToken(USER_ID_CLIENT, "ROLE_CLIENT", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        //test + validate
        mvc.perform(
                post(String.format(URL, PAYMENT_UUID_EXISTS))
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("result"))
                .andExpect(model().attribute("result", message.getMessage("payment.pending")));
    }

}