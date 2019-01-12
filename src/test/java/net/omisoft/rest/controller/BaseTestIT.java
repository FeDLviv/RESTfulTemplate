package net.omisoft.rest.controller;

import com.google.common.io.BaseEncoding;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.omisoft.rest.HibernateQueryCounterInterceptor;
import net.omisoft.rest.SQLQueryCounter;
import net.omisoft.rest.configuration.MessageSourceConfiguration;
import net.omisoft.rest.model.AccessTokenEntity;
import net.omisoft.rest.model.UserEntity;
import net.omisoft.rest.repository.AccessTokenRepository;
import net.omisoft.rest.repository.UserRepository;
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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.spec.SecretKeySpec;
import javax.transaction.Transactional;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Logger;

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

    private static final Locale LOCALE = new Locale("en", "UK");
    private static final Logger LOGGER = Logger.getLogger(BaseTestIT.class.getName());
    private static final Random RANDOM = new Random();

    protected static final String URI = API_V1_BASE_PATH;

    public static final String WRONG_TOKEN = "dgdfg";
    public static final String PASSWORD_EXISTS = "1111";
    public static final String WRONG_PASSWORD = "djfkl327";
    public static final String EMAIL_EXISTS = "fed.lviv@gmail.com";
    public static final String EMAIL_NOT_EXISTS = "asd@gmail.com";
    public static final long USER_ID_ADMIN = 1;
    public static final long USER_ID_CLIENT = 2;
    public static final long USER_ID_NOT_EXISTS = 333;
    public static final String PAYMENT_UUID_EXISTS = "cc17f52bf21f4d8392d7bc28936cd621";
    public static final String PAYMENT_UUID_WITH_RESPONSE_BODY = "6414d12b1f144b45bc105fc5ef5d64bc";
    public static final String PAYMENT_UUID_NOT_EXISTS = "3e3df2f4a6004ab6b2a73a933ba5e167";

    @TestConfiguration
    public static class BaseTestITContextConfiguration {

        @Bean
        public SQLQueryCounter sqlQueryCounter() {
            return new HibernateQueryCounterInterceptor();
        }

    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected MessageSourceConfiguration message;

    @Autowired
    protected SQLQueryCounter sqlQueryCounter;

    @Value("${app.token.secret}")
    protected String secret;

    @Value("${app.token.duration}")
    protected String tokenDuration;

    protected String token;

    protected Validator validator;

    @BeforeClass
    public static void init() {
        Locale.setDefault(LOCALE);
    }

    @Before
    public void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    protected String generateToken(long idUser, String role, Date expire) {
        byte[] decodedKey = BaseEncoding.base64().decode(secret);
        return Jwts.builder()
                .setId(String.valueOf(idUser))
                .setExpiration(expire)
                .claim("random", RANDOM.nextInt(9999))
                .claim("role", role)
                .signWith(SignatureAlgorithm.HS512, new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"))
                .compact();
    }

    protected String generateAndInsertToken(long idUser, String role, Date expire) {
        String token = generateToken(idUser, role, expire);
        insertToken(token, idUser, expire);
        return token;
    }

    protected void insertToken(String token, long idUser, Date expired) {
        LOGGER.info("START - INSERT JWT TOKEN TO DB");
        UserEntity user = userRepository.findById(idUser).orElseThrow(NullPointerException::new);
        accessTokenRepository.save(new AccessTokenEntity(token, user, expired));
        LOGGER.info("STOP - INSERT JWT TOKEN TO DB");
    }

    @Test
    public abstract void expireToken() throws Exception;

    @Test
    public abstract void wrongToken() throws Exception;

    @Test
    public abstract void tokenNotExists() throws Exception;

    @Test
    @Rollback
    public abstract void notAuthorized() throws Exception;

    @Test
    @Rollback
    public abstract void authorizedAdmin() throws Exception;

    @Test
    @Rollback
    public abstract void authorizedClient() throws Exception;

}