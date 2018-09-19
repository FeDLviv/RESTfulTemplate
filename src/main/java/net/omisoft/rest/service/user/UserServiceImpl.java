package net.omisoft.rest.service.user;

import lombok.AllArgsConstructor;
import net.omisoft.rest.configuration.MessageSourceConfiguration;
import net.omisoft.rest.configuration.PropertiesConfiguration;
import net.omisoft.rest.exception.BadRequestException;
import net.omisoft.rest.exception.PermissionException;
import net.omisoft.rest.exception.ResourceNotFoundException;
import net.omisoft.rest.model.UserEntity;
import net.omisoft.rest.model.base.UserRole;
import net.omisoft.rest.pojo.AuthResponse;
import net.omisoft.rest.pojo.PasswordRequest;
import net.omisoft.rest.repository.AccessTokenRepository;
import net.omisoft.rest.repository.UserRepository;
import net.omisoft.rest.service.security.JWTService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccessTokenRepository accessTokenRepository;
    private final JWTService jwtService;
    private final PropertiesConfiguration propertiesConfiguration;
    private final MessageSourceConfiguration message;

    @Override
    @Transactional
    public void deleteById(long idUser, UserEntity currentUser) {
        if (idUser == currentUser.getId() || currentUser.getRole().equals(UserRole.ROLE_ADMIN)) {
            try {
                userRepository.deleteById(idUser);
            } catch (EmptyResultDataAccessException ex) {
                throw new ResourceNotFoundException(message.getMessage("exception.user.not_exists"));
            }
        } else {
            throw new PermissionException(message.getMessage("exception.auth.permission"));
        }
    }

    @Override
    @Transactional
    public AuthResponse updatePassword(UserEntity currentUser, PasswordRequest data) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (encoder.matches(data.getOldPassword(), currentUser.getPassword())) {
            currentUser.setPassword(encoder.encode(data.getNewPassword()));
            userRepository.updatePassword(currentUser);
            accessTokenRepository.removeAllByUser(currentUser);
            return AuthResponse.builder()
                    .token(jwtService.getToken(currentUser))
                    .duration(propertiesConfiguration.getToken().getDuration())
                    .build();
        } else {
            throw new BadRequestException(message.getMessage("exception.old_password.wrong"));
        }
    }

}