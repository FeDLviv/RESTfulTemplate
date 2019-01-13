package net.omisoft.rest.controller.payment;

import net.omisoft.rest.controller.BaseTestIT;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Date;

import static net.omisoft.rest.ApplicationConstants.AUTH_HEADER;
import static net.omisoft.rest.ApplicationConstants.TOKEN_PREFIX;
import static net.omisoft.rest.service.interkassa.InterkassaService.DEFAULT_EMAIL;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
INTEGRATION TEST
 */

public class CheckoutTestIT extends BaseTestIT {

    private static final String URL = "/payment/checkout?amount=%s&email=%s";

    @Test
    public void wrongAmountFormat() throws Exception {
        incorrectValidationRequestParams("10.003", null, "amount -");
    }

    @Test
    public void amountZero() throws Exception {
        incorrectValidationRequestParams("0", null, "amount -");
    }

    @Test
    public void amountNegative() throws Exception {
        incorrectValidationRequestParams("-5", null, "amount -");
    }

    @Test
    public void amountNull() throws Exception {
        incorrectValidationRequestParams(null, null, null);
    }

    @Test
    public void wrongEmailFormat() throws Exception {
        incorrectValidationRequestParams("10", "mygmail.com", "email -");
    }

    @Override
    public void expireToken() throws Exception {
        //prepare
        String token = generateToken(USER_ID_ADMIN, "ROLE_ADMIN", new Date());
        String amount = "10";
        //test + validate
        mvc.perform(
                get(String.format(URL.replace("&email=%s", ""), amount))
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.token.expire")));
    }

    @Override
    public void wrongToken() throws Exception {
        //prepare
        String amount = "10";
        //test + validate
        mvc.perform(
                get(String.format(URL.replace("&email=%s", ""), amount))
                        .header(AUTH_HEADER, TOKEN_PREFIX + WRONG_TOKEN)
        )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.token.wrong")));
    }

    @Override
    public void tokenNotExists() throws Exception {
        //prepare
        String token = generateToken(USER_ID_ADMIN, "ROLE_ADMIN", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        String amount = "10";
        //test + validate
        mvc.perform(
                get(String.format(URL.replace("&email=%s", ""), amount))
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.token.not_exists")));
    }

    @Override
    public void notAuthorized() throws Exception {
        //prepare
        String amount = "10";
        //test + validate
        mvc.perform(
                get(String.format(URL.replace("&email=%s", ""), amount)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.auth.required")));
    }

    @Override
    public void authorizedAdmin() throws Exception {
        //prepare
        token = generateAndInsertToken(USER_ID_ADMIN, "ROLE_ADMIN", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        String amount = "9.33";
        String email = "krl@gmail.com";
        //test + validate
        mvc.perform(
                get(String.format(URL, amount, email))
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("checkout"))
                .andExpect(model().attribute("data", hasEntry("ik_cli", email)))
                .andExpect(model().attribute("data", hasEntry(equalTo("ik_am"), equalTo(amount))));
    }

    @Override
    public void authorizedClient() throws Exception {
        //prepare
        token = generateAndInsertToken(USER_ID_CLIENT, "ROLE_CLIENT", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        String amount = "10";
        //test + validate
        mvc.perform(
                get(String.format(URL.replace("&email=%s", ""), amount))
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("checkout"))
                .andExpect(model().attribute("data", hasEntry("ik_cli", DEFAULT_EMAIL)))
                .andExpect(model().attribute("data", hasEntry(equalTo("ik_am"), equalTo(amount))));
    }

    private void incorrectValidationRequestParams(String amount, String email, String msgStart) throws Exception {
        //prepare
        token = generateAndInsertToken(USER_ID_CLIENT, "ROLE_CLIENT", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        String url = String.format(URL, amount, email);
        url = url.replaceAll("amount=null&|&email=null", "");
        ResultMatcher matcher = msgStart == null ? jsonPath("$").doesNotExist() : jsonPath("$.message").value(startsWith(msgStart));
        //test + validate
        mvc.perform(
                get(url)
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
        )
                .andExpect(status().isBadRequest())
                .andExpect(matcher);
    }

}