package net.omisoft.rest.controller.security;

import com.google.common.io.BaseEncoding;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import net.omisoft.rest.configuration.MessageSourceConfiguration;
import net.omisoft.rest.pojo.AuthRequest;
import net.omisoft.rest.pojo.AuthResponse;
import net.omisoft.rest.pojo.CustomMessage;
import org.apache.logging.log4j.util.Strings;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.crypto.spec.SecretKeySpec;
import javax.transaction.Transactional;

import static net.omisoft.rest.ApplicationConstants.API_V1_BASE_PATH;
import static net.omisoft.rest.controller.BaseTestIT.*;
import static org.assertj.core.api.Assertions.assertThat;

/*
INTEGRATION TEST
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AuthTestIT {

    private static final String URL = API_V1_BASE_PATH + "security/auth";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MessageSourceConfiguration message;

    @Value("${app.token.secret}")
    private String secret;

    @Value("${app.token.duration}")
    private String tokenDuration;

    @Test
    public void emptyEmail() {
        notValidEmail(Strings.EMPTY);
    }

    @Test
    public void incorrectEmail() {
        notValidEmail("fdsfs");
    }

    @Test
    public void notExistsEmail() {
        wrongCredential(EMAIL_NOT_EXISTS, PASSWORD_EXISTS);
    }

    @Test
    public void wrongPassword() {
        wrongCredential(EMAIL_EXISTS, WRONG_PASSWORD);
    }

    @Test
    public void emptyPassword() {
        //prepare
        AuthRequest auth = AuthRequest.builder().email(EMAIL_EXISTS).password(Strings.EMPTY).build();
        //test
        HttpEntity<AuthRequest> request = new HttpEntity<>(auth);
        ResponseEntity<CustomMessage> response = restTemplate.postForEntity(
                URL,
                request,
                CustomMessage.class
        );
        //validate
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getProperty()).startsWith("password");
        assertThat(response.getBody().getMessage()).isNotEmpty();
    }

    @Test
    @Rollback
    public void successful() {
        //prepare
        AuthRequest auth = AuthRequest.builder().email(EMAIL_EXISTS).password(PASSWORD_EXISTS).build();
        //test
        HttpEntity<AuthRequest> request = new HttpEntity<>(auth);
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                URL,
                request,
                AuthResponse.class
        );
        //validate
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String token = response.getBody().getToken();
        byte[] decodedKey = BaseEncoding.base64().decode(secret);
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES")).parseClaimsJws(token);
        Claims body = claimsJws.getBody();
        assertThat(body.getId()).isEqualTo("1");
        assertThat(body.get("role")).isEqualTo("ROLE_ADMIN");
        assertThat(body.get("random")).isNotNull();
        assertThat(response.getBody().getDuration()).isEqualTo(Long.parseLong(tokenDuration));
    }

    private void wrongCredential(String email, String password) {
        //prepare
        AuthRequest auth = AuthRequest.builder().email(email).password(password).build();
        //test
        HttpEntity<AuthRequest> request = new HttpEntity<>(auth);
        ResponseEntity<CustomMessage> response = restTemplate.postForEntity(
                URL,
                request,
                CustomMessage.class
        );
        //validate
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).isEqualTo(message.getMessage("exception.credentials.wrong"));
    }

    private void notValidEmail(String email) {
        //prepare
        AuthRequest auth = AuthRequest.builder().email(email).password(PASSWORD_EXISTS).build();
        //test
        HttpEntity<AuthRequest> request = new HttpEntity<>(auth);
        ResponseEntity<CustomMessage> response = restTemplate.postForEntity(
                URL,
                request,
                CustomMessage.class
        );
        //validate
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getProperty()).startsWith("email");
        assertThat(response.getBody().getMessage()).isNotEmpty();
    }

}