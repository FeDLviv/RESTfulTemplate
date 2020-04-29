package net.omisoft.rest.controller.payment;

import net.omisoft.rest.controller.BaseTestIT;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;

import static net.omisoft.rest.ApplicationConstants.AUTH_HEADER;
import static net.omisoft.rest.ApplicationConstants.TOKEN_PREFIX;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/*
INTEGRATION TEST
 */

public class FailTestIT extends BaseTestIT {

    private static final String URL = "/payment/fail/%s";

    @Test
    public void uuidIsEmpty() throws Exception {
        //test + validate
        mvc.perform(
                MockMvcRequestBuilders.post(URL.replaceAll("%s", ""))
        )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotHaveJsonPath());
    }

    @Test
    public void uuidIsNotExists() throws Exception {
        //test + validate
        mvc.perform(
                MockMvcRequestBuilders.post(String.format(URL, PAYMENT_UUID_NOT_EXISTS))
        )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(HttpStatus.NOT_FOUND.getReasonPhrase()));
    }

    @Override
    public void expireToken() throws Exception {
        //prepare
        String token = generateAdminToken(USER_ID_ADMIN, new Date());
        //test + validate
        mvc.perform(
                MockMvcRequestBuilders.post(String.format(URL, PAYMENT_UUID_EXISTS))
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(message.getMessage("exception.token.expire")));
    }

    @Override
    public void wrongToken() throws Exception {
        //test + validate
        mvc.perform(
                MockMvcRequestBuilders.post(String.format(URL, PAYMENT_UUID_EXISTS))
                        .header(AUTH_HEADER, TOKEN_PREFIX + WRONG_TOKEN)
        )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(message.getMessage("exception.token.wrong")));
    }

    @Override
    public void tokenNotExists() throws Exception {
        //prepare
        String token = generateAdminToken(USER_ID_ADMIN, new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        //test + validate
        mvc.perform(
                post(String.format(URL, PAYMENT_UUID_EXISTS))
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(message.getMessage("exception.token.not_exists")));
    }

    @Override
    public void notAuthorized() throws Exception {
        //test + validate
        mvc.perform(
                MockMvcRequestBuilders.post(String.format(URL, PAYMENT_UUID_EXISTS))
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("result"))
                .andExpect(MockMvcResultMatchers.model().attribute("result", message.getMessage("payment.fail")));
    }

    @Override
    public void authorizedAdmin() throws Exception {
        //prepare
        token = generateAndInsertAdminToken(USER_ID_ADMIN, new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        //test + validate
        mvc.perform(
                MockMvcRequestBuilders.post(String.format(URL, PAYMENT_UUID_EXISTS))
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("result"))
                .andExpect(MockMvcResultMatchers.model().attribute("result", message.getMessage("payment.fail")));
    }

    @Override
    public void authorizedClient() throws Exception {
        //prepare
        token = generateAndInsertClientToken(USER_ID_CLIENT, new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        //test + validate
        mvc.perform(
                MockMvcRequestBuilders.post(String.format(URL, PAYMENT_UUID_EXISTS))
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("result"))
                .andExpect(MockMvcResultMatchers.model().attribute("result", message.getMessage("payment.fail")));
    }

}