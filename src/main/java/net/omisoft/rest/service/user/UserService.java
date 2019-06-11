package net.omisoft.rest.service.user;

import net.omisoft.rest.model.UserEntity;
import net.omisoft.rest.pojo.AuthResponse;
import net.omisoft.rest.pojo.PasswordRequest;
import net.omisoft.rest.repository.specification.UserEmailAndRolesSpecification;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;

public interface UserService {

    UserEntity create(UserEntity data);

    @CacheEvict(value = "tokens", allEntries = true)
    void deleteById(long idUser, UserEntity currentUser);

    List<UserEntity> getUsers(UserEmailAndRolesSpecification userSpecification);

    @CacheEvict(value = "tokens", allEntries = true)
    AuthResponse updatePassword(UserEntity currentUser, PasswordRequest data);

}