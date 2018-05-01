package net.omisoft.rest.service.security;

import com.google.common.io.BaseEncoding;
import io.jsonwebtoken.*;
import net.omisoft.rest.configuration.security.UserAuthentication;
import net.omisoft.rest.exception.BadRequestException;
import net.omisoft.rest.exception.UnauthorizedException;
import net.omisoft.rest.model.UserEntity;
import net.omisoft.rest.pojo.AuthRequest;
import net.omisoft.rest.repository.UserRepository;
import net.omisoft.rest.util.MessageByLocaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.util.Date;

import static net.omisoft.rest.ApplicationConstants.TOKEN_PREFIX;

@Service
public class JWTServiceImpl implements JWTService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private MessageByLocaleService message;

    @Value("${app.token.secret}")
    private String secret;

    @Value("${app.token.duration}")
    private String duration;

    private SecretKey key;

    @PostConstruct
    private void initialize() {
        byte[] decodedKey = BaseEncoding.base64().decode(secret);
        key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    @Override
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
                .setExpiration(new Date(new Date().getTime() + Long.parseLong(duration)))
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

}

