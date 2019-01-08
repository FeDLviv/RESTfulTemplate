package net.omisoft.rest.repository.user;

import net.omisoft.rest.model.UserEntity;
import net.omisoft.rest.repository.BaseTestData;
import net.omisoft.rest.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static net.omisoft.rest.controller.BaseTestIT.USER_ID_ADMIN;
import static net.omisoft.rest.controller.BaseTestIT.WRONG_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;

public class UpdatePasswordTest extends BaseTestData {

    @Autowired
    private UserRepository repository;

    @Test(expected = SpelEvaluationException.class)
    public void updatePasswordIfUserNull() {
        //test
        repository.updatePassword(null);
    }

    @Test
    public void updatePasswordIfUserNotExists() {
        //prepare
        UserEntity user = entityManager.find(UserEntity.class, USER_ID_ADMIN);
        entityManager.remove(user);
        entityManager.flush();
        //test
        int result = repository.updatePassword(user);
        //validate
        assertThat(result)
                .isEqualTo(0);
    }

    @Test
    public void updatePassword() {
        //prepare
        UserEntity user = entityManager.find(UserEntity.class, USER_ID_ADMIN);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(WRONG_PASSWORD));
        //test
        int result = repository.updatePassword(user);
        //validate
        assertThat(result)
                .isEqualTo(1);
        entityManager.clear();
        UserEntity entity = entityManager.find(UserEntity.class, USER_ID_ADMIN);
        assertThat(encoder.matches(WRONG_PASSWORD, entity.getPassword()))
                .isEqualTo(true);

    }

}