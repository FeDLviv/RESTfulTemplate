package net.omisoft.rest.service.security;

import net.omisoft.rest.exception.UnauthorizedException;
import net.omisoft.rest.model.UserEntity;
import net.omisoft.rest.pojo.AuthRequest;

public interface JWTService {

    void setAuthentication(String header) throws UnauthorizedException;

    String getToken(AuthRequest credential);

    String getToken(UserEntity user);

}