package net.omisoft.rest.service.security;

import com.google.common.io.BaseEncoding;
import io.jsonwebtoken.*;
import net.omisoft.rest.configuration.MessageSourceConfiguration;
import net.omisoft.rest.configuration.PropertiesConfiguration;
import net.omisoft.rest.configuration.security.UserAuthentication;
import net.omisoft.rest.exception.BadRequestException;
import net.omisoft.rest.exception.UnauthorizedException;
import net.omisoft.rest.model.AccessTokenEntity;
import net.omisoft.rest.model.UserEntity;
import net.omisoft.rest.pojo.AuthRequest;
import net.omisoft.rest.repository.AccessTokenRepository;
import net.omisoft.rest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Random;

import static net.omisoft.rest.ApplicationConstants.TOKEN_PREFIX;

//http://jwt.calebb.net
@Service
public class JWTServiceImpl implements JWTService {

    private final UserRepository userRepository;
    private final AccessTokenRepository accessTokenRepository;
    private final MessageSourceConfiguration message;
    private final PropertiesConfiguration propertiesConfiguration;
    private final SecretKey key;
    private final Random random;

    @Autowired
    public JWTServiceImpl(UserRepository userRepository, AccessTokenRepository accessTokenRepository, MessageSourceConfiguration message, PropertiesConfiguration propertiesConfiguration) {
        this.userRepository = userRepository;
        this.accessTokenRepository = accessTokenRepository;
        this.message = message;
        this.propertiesConfiguration = propertiesConfiguration;
        byte[] decodedKey = BaseEncoding.base64().decode(propertiesConfiguration.getToken().getSecret());
        this.key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        this.random = new Random();
    }

    @Override
    @Transactional(readOnly = true)
    public void setAuthentication(String header) throws UnauthorizedException {
        String token = header.substring(TOKEN_PREFIX.length());
        validToken(token);
        AccessTokenEntity entity = accessTokenRepository.getByTokenJoinUser(token)
                .orElseThrow(() -> new UnauthorizedException(message.getMessage("exception.token.not_exists")));
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(entity.getUser()));
    }

    private void validToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            Claims body = claimsJws.getBody();
            Long.parseLong(body.getId());
            if (body.get("role") == null) {
                throw new UnauthorizedException(message.getMessage("exception.token.wrong"));
            }
            Integer.parseInt(body.get("random").toString());
        } catch (ExpiredJwtException ex) {
            throw new UnauthorizedException(message.getMessage("exception.token.expire"));
        } catch (Exception e) {
            throw new UnauthorizedException(message.getMessage("exception.token.wrong"));
        }
    }

    @Override
    @Transactional
    public String getToken(AuthRequest credential) {
        UserEntity user = userRepository.findByEmailIgnoreCase(credential.getEmail())
                .orElseThrow(() -> new BadRequestException(message.getMessage("exception.credentials.wrong")));
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (encoder.matches(credential.getPassword(), user.getPassword())) {
            Date expire = new Date(new Date().getTime() + propertiesConfiguration.getToken().getDuration());
            String token = generateAccessToken(user.getId(), user.getRole(), expire);
            accessTokenRepository.save(new AccessTokenEntity(token, user, expire));
            return token;
        } else {
            throw new BadRequestException(message.getMessage("exception.credentials.wrong"));
        }
    }

    @Override
    @Transactional
    public String getToken(UserEntity user) {
        Date expire = new Date(new Date().getTime() + propertiesConfiguration.getToken().getDuration());
        String token = generateAccessToken(user.getId(), user.getRole(), expire);
        accessTokenRepository.save(new AccessTokenEntity(token, user, expire));
        return token;
    }

    private String generateAccessToken(long id, String role, Date expire) {
        return Jwts.builder()
                .setId(String.valueOf(id))
                .setExpiration(expire)
                .claim("random", random.nextInt(9999))
                .claim("role", role)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

}