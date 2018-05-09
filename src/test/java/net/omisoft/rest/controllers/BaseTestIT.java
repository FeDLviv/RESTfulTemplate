package net.omisoft.rest.controllers;

import com.google.common.io.BaseEncoding;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.omisoft.rest.util.MessageByLocaleService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.spec.SecretKeySpec;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Date;
import java.util.Locale;

import static net.omisoft.rest.ApplicationConstants.API_V1_BASE_PATH;

/*
INTEGRATION TEST
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
@Transactional
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class BaseTestIT {

    private static final Locale locale = new Locale("en", "UK");

    protected static final String URI = API_V1_BASE_PATH;

    public static final String WRONG_TOKEN = "dgdfg";
    public static final String WRONG_PASSWORD = "djfkl327";
    public static final long USER_ID_NOT_EXISTS = 333;
    public static final String EMAIL_EXISTS = "fed.lviv@gmail.com";
    public static final String PASSWORD_EXISTS = "1111";
    public static final String EMAIL_NOT_EXISTS = "asd@gmail.com";

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected MessageByLocaleService message;

    @Value("${app.token.secret}")
    protected String secret;

    @Value("${app.token.duration}")
    protected String tokenDuration;

    protected String token;

    protected Validator validator;

    @BeforeClass
    public static void init() {
        Locale.setDefault(locale);
    }

    @Before
    public void setup() {
        token = generateToken(1, new Date(new Date().getTime() + Long.parseLong(tokenDuration)));
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    protected String generateToken(long id, Date expiration) {
        byte[] decodedKey = BaseEncoding.base64().decode(secret);
        return Jwts.builder()
                .setId(String.valueOf(id))
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"))
                .compact();
    }

    @Test
    public abstract void expireToken() throws Exception;

    @Test
    public abstract void wrongToken() throws Exception;

    @Test
    public abstract void authWrongToken() throws Exception;

    @Test
    @Rollback
    public abstract void notAuthorized() throws Exception;

}
