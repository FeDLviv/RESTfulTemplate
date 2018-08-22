package net.omisoft.rest.service.security;

import com.google.common.io.BaseEncoding;
import io.jsonwebtoken.*;
import net.omisoft.rest.configuration.MessageSourceConfiguration;
import net.omisoft.rest.configuration.PropertiesConfiguration;
import net.omisoft.rest.configuration.security.UserAuthentication;
import net.omisoft.rest.exception.BadRequestException;
import net.omisoft.rest.exception.UnauthorizedException;
import net.omisoft.rest.model.UserEntity;
import net.omisoft.rest.pojo.AuthRequest;
import net.omisoft.rest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

import static net.omisoft.rest.ApplicationConstants.TOKEN_PREFIX;

//http://jwt.calebb.net
@Service
public class JWTServiceImpl implements JWTService {

    private final UserRepository repository;
    private final MessageSourceConfiguration message;
    private final PropertiesConfiguration propertiesConfiguration;
    private final SecretKey key;

    @Autowired
    public JWTServiceImpl(UserRepository repository, MessageSourceConfiguration message, PropertiesConfiguration propertiesConfiguration) {
        this.repository = repository;
        this.message = message;
        this.propertiesConfiguration = propertiesConfiguration;
        byte[] decodedKey = BaseEncoding.base64().decode(propertiesConfiguration.getToken().getSecret());
        this.key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    @Override
    @Transactional(readOnly = true)
    public void setAuthentication(String header) throws UnauthorizedException {
        UserEntity entity = repository.findById(extractUserId(header))
                .orElseThrow(() -> new UnauthorizedException(message.getMessage("exception.auth.wrong")));
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(entity));
    }

    private long extractUserId(String token) {
        try {
            token = token.substring(TOKEN_PREFIX.length());
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            Claims body = claimsJws.getBody();
            return Long.parseLong(body.getId());
        } catch (ExpiredJwtException ex) {
            throw new UnauthorizedException(message.getMessage("exception.token.expire"));
        } catch (Exception e) {
            throw new UnauthorizedException(message.getMessage("exception.token.wrong"));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String getToken(AuthRequest credential) {
        UserEntity entity = repository.findByEmailIgnoreCase(credential.getEmail())
                .orElseThrow(() -> new BadRequestException(message.getMessage("exception.wrong.credentials")));
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (encoder.matches(credential.getPassword(), entity.getPassword())) {
            return generateAccessToken(entity.getId());
        } else {
            throw new BadRequestException(message.getMessage("exception.wrong.credentials"));
        }
    }

    private String generateAccessToken(long id) {
        return Jwts.builder()
                .setId(String.valueOf(id))
                .setExpiration(new Date(new Date().getTime() + propertiesConfiguration.getToken().getDuration()))
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

}