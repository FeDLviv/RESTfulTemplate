package net.omisoft.rest.service.user;

import net.omisoft.rest.model.UserEntity;
import net.omisoft.rest.pojo.AuthResponse;
import net.omisoft.rest.pojo.PasswordRequest;
import org.springframework.cache.annotation.CacheEvict;

public interface UserService {

    @CacheEvict(value = "tokens", allEntries = true)
    void deleteById(long idUser, UserEntity currentUser);

    @CacheEvict(value = "tokens", allEntries = true)
    AuthResponse updatePassword(UserEntity currentUser, PasswordRequest data);

}