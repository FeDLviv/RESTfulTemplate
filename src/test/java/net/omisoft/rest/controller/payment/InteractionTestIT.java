package net.omisoft.rest.controller.payment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import net.omisoft.rest.controller.BaseTestIT;
import net.omisoft.rest.service.interkassa.InterkassaService;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.*;

import static net.omisoft.rest.ApplicationConstants.AUTH_HEADER;
import static net.omisoft.rest.ApplicationConstants.TOKEN_PREFIX;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
INTEGRATION TEST
 */

public class InteractionTestIT extends BaseTestIT {

    private static final String URL = "/payment/interaction/%s";

    private static MultiValueMap<String, String> getParams() throws IOException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> data = mapper.readValue("{\"ik_co_id\":\"5bea9daf3c1eaf93328b4569\",\"ik_co_prs_id\":\"300624665074\",\"ik_inv_id\":\"121067533\",\"ik_inv_st\":\"success\",\"ik_inv_crt\":\"2018-11-30 17:08:05\",\"ik_inv_prc\":\"2018-11-30 17:08:05\",\"ik_trn_id\":\"\",\"ik_pm_no\":\"6414d12b1f144b45bc105fc5ef5d64bc\",\"ik_pw_via\":\"test_interkassa_test_xts\",\"ik_am\":\"99.23\",\"ik_co_rfn\":\"96.2531\",\"ik_ps_price\":\"99.23\",\"ik_cur\":\"UAH\",\"ik_desc\":\"Pay for team\",\"ik_cli\":\"fed.lviv@gmail.com\",\"ik_sign\":\"pvJheD3jFJAGVVpzE1d/ZQ==\"}", new TypeReference<Map<String, String>>() {
        });
        params.setAll(data);
        return params;
    }

    private static String getRandomIP() {
        Random r = new Random();
        String ip;
        do {
            ip = r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
        } while (Arrays.asList(InterkassaService.IP).contains(ip));
        return ip;
    }

    private static RequestPostProcessor remoteAddr(final String remoteAddr) {
        return new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setRemoteAddr(remoteAddr);
                return request;
            }
        };
    }

    @Test
    public void uuidNull() throws Exception {
        //test + validate
        mvc.perform(
                post(URL.replaceAll("%s", ""))
                        .params(getParams())
                        .with(remoteAddr(InterkassaService.IP[new Random().nextInt(InterkassaService.IP.length)]))
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotHaveJsonPath());
    }

    @Test
    public void dataNull() throws Exception {
        //test + validate
        mvc.perform(
                post(String.format(URL, PAYMENT_UUID_WITH_RESPONSE_BODY))
                        .with(remoteAddr(InterkassaService.IP[new Random().nextInt(InterkassaService.IP.length)]))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    public void ipRemoteWrong() throws Exception {
        //test + validate
        mvc.perform(
                post(String.format(URL, PAYMENT_UUID_WITH_RESPONSE_BODY))
                        .params(getParams())
                        .with(remoteAddr(getRandomIP()))
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(HttpStatus.NOT_FOUND.getReasonPhrase()));
    }

    @Test
    public void ipHeaderWrong() throws Exception {
        //test + validate
        mvc.perform(
                post(String.format(URL, PAYMENT_UUID_WITH_RESPONSE_BODY))
                        .header(HttpHeaders.X_FORWARDED_FOR, getRandomIP())
                        .params(getParams())
                        .with(remoteAddr(InterkassaService.IP[new Random().nextInt(InterkassaService.IP.length)]))
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(HttpStatus.NOT_FOUND.getReasonPhrase()));
    }

    @Test
    public void signatureWrong() throws Exception {
        //prepare
        MultiValueMap<String, String> params = getParams();
        StringBuilder wrongSignature = new StringBuilder(params.get("ik_sign").get(0));
        int index = new Random().nextInt(wrongSignature.length());
        wrongSignature.setCharAt(index, wrongSignature.charAt(index) == 'x' ? 'y' : 'x');
        params.get("ik_sign").set(0, wrongSignature.toString());
        //test + validate
        mvc.perform(
                post(String.format(URL, PAYMENT_UUID_WITH_RESPONSE_BODY))
                        .params(params)
                        .with(remoteAddr(InterkassaService.IP[new Random().nextInt(InterkassaService.IP.length)]))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.payment.digital_signature")));
    }

    @Test
    public void uuidNotEquals() throws Exception {
        //prepare
        MultiValueMap<String, String> params = getParams();
        params.get("ik_pm_no").set(0, UUID.randomUUID().toString().replaceAll("-", ""));
        SortedMap<String, String> map = new TreeMap<>(params.toSingleValueMap());
        map.remove("ik_sign");
        String result = String.join(":", map.values());
        result += ":" + propertiesConfiguration.getInterkassa().getTestKey();
        byte[] digest = MessageDigest.getInstance("MD5").digest(result.getBytes());
        map.put("ik_sign", Base64.getEncoder().encodeToString(digest));
        params.setAll(map);
        //test + validate
        mvc.perform(
                post(String.format(URL, PAYMENT_UUID_WITH_RESPONSE_BODY))
                        .params(params)
                        .with(remoteAddr(InterkassaService.IP[new Random().nextInt(InterkassaService.IP.length)]))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.payment.wrong_id")));
    }

    @Test
    public void uuidNotExists() throws Exception {
        //prepare
        MultiValueMap<String, String> params = getParams();
        params.get("ik_pm_no").set(0, PAYMENT_UUID_NOT_EXISTS);
        SortedMap<String, String> map = new TreeMap<>(params.toSingleValueMap());
        map.remove("ik_sign");
        String result = String.join(":", map.values());
        result += ":" + propertiesConfiguration.getInterkassa().getTestKey();
        byte[] digest = MessageDigest.getInstance("MD5").digest(result.getBytes());
        map.put("ik_sign", Base64.getEncoder().encodeToString(digest));
        params.setAll(map);
        //test + validate
        mvc.perform(
                post(String.format(URL, PAYMENT_UUID_NOT_EXISTS))
                        .params(params)
                        .with(remoteAddr(InterkassaService.IP[new Random().nextInt(InterkassaService.IP.length)]))
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.payment.not_exists")));
    }

    @Test
    public void sumNotEquals() throws Exception {
        //prepare
        MultiValueMap<String, String> params = getParams();
        params.get("ik_am").set(0, "99.29");
        SortedMap<String, String> map = new TreeMap<>(params.toSingleValueMap());
        map.remove("ik_sign");
        String result = String.join(":", map.values());
        result += ":" + propertiesConfiguration.getInterkassa().getTestKey();
        byte[] digest = MessageDigest.getInstance("MD5").digest(result.getBytes());
        map.put("ik_sign", Base64.getEncoder().encodeToString(digest));
        params.setAll(map);
        //test + validate
        mvc.perform(
                post(String.format(URL, PAYMENT_UUID_WITH_RESPONSE_BODY))
                        .params(params)
                        .with(remoteAddr(InterkassaService.IP[new Random().nextInt(InterkassaService.IP.length)]))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.payment.sum")));
    }

    @Test
    public void officeWrong() throws Exception {
        //prepare
        MultiValueMap<String, String> params = getParams();
        StringBuilder wrongOffice = new StringBuilder(params.get("ik_co_id").get(0));
        int index = new Random().nextInt(wrongOffice.length());
        wrongOffice.setCharAt(index, wrongOffice.charAt(index) == 'x' ? 'y' : 'x');
        params.get("ik_co_id").set(0, wrongOffice.toString());
        SortedMap<String, String> map = new TreeMap<>(params.toSingleValueMap());
        map.remove("ik_sign");
        String result = String.join(":", map.values());
        result += ":" + propertiesConfiguration.getInterkassa().getTestKey();
        byte[] digest = MessageDigest.getInstance("MD5").digest(result.getBytes());
        map.put("ik_sign", Base64.getEncoder().encodeToString(digest));
        params.setAll(map);
        //test + validate
        mvc.perform(
                post(String.format(URL, PAYMENT_UUID_WITH_RESPONSE_BODY))
                        .params(params)
                        .with(remoteAddr(InterkassaService.IP[new Random().nextInt(InterkassaService.IP.length)]))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.payment.wrong_id_office")));
    }

    @Override
    public void expireToken() throws Exception {
        //prepare
        String token = generateToken(USER_ID_ADMIN, "ROLE_ADMIN", new Date());
        //test + validate
        mvc.perform(
                post(String.format(URL, PAYMENT_UUID_WITH_RESPONSE_BODY))
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
                        .params(getParams())
                        .with(remoteAddr(InterkassaService.IP[new Random().nextInt(InterkassaService.IP.length)]))
        )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.token.expire")));
    }

    @Override
    public void wrongToken() throws Exception {
        //test + validate
        mvc.perform(
                post(String.format(URL, PAYMENT_UUID_WITH_RESPONSE_BODY))
                        .header(AUTH_HEADER, TOKEN_PREFIX + WRONG_TOKEN)
                        .params(getParams())
                        .with(remoteAddr(InterkassaService.IP[new Random().nextInt(InterkassaService.IP.length)]))
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
                post(String.format(URL, PAYMENT_UUID_WITH_RESPONSE_BODY))
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
                        .params(getParams())
                        .with(remoteAddr(InterkassaService.IP[new Random().nextInt(InterkassaService.IP.length)]))
        )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(message.getMessage("exception.token.not_exists")));
    }

    @Override
    public void notAuthorized() throws Exception {
        //test + validate
        mvc.perform(
                post(String.format(URL, PAYMENT_UUID_WITH_RESPONSE_BODY))
                        .params(getParams())
                        .with(remoteAddr(InterkassaService.IP[new Random().nextInt(InterkassaService.IP.length)]))
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").doesNotHaveJsonPath());
    }

    @Override
    public void authorizedAdmin() throws Exception {
        //prepare
        token = generateAndInsertToken(USER_ID_ADMIN, "ROLE_ADMIN", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        //test + validate
        mvc.perform(
                post(String.format(URL, PAYMENT_UUID_WITH_RESPONSE_BODY))
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
                        .header(HttpHeaders.X_FORWARDED_FOR, InterkassaService.IP[new Random().nextInt(InterkassaService.IP.length)])
                        .params(getParams())
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").doesNotHaveJsonPath());
    }

    @Override
    public void authorizedClient() throws Exception {
        //prepare
        token = generateAndInsertToken(USER_ID_CLIENT, "ROLE_CLIENT", new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        //test + validate
        mvc.perform(
                post(String.format(URL, PAYMENT_UUID_WITH_RESPONSE_BODY))
                        .header(AUTH_HEADER, TOKEN_PREFIX + token)
                        .params(getParams())
                        .with(remoteAddr(InterkassaService.IP[new Random().nextInt(InterkassaService.IP.length)]))
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").doesNotHaveJsonPath());
    }

}