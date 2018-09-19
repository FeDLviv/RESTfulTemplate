package net.omisoft.rest.repository;

import net.omisoft.rest.controller.BaseTestIT;
import net.omisoft.rest.model.UserEntity;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.NoSuchElementException;

import static net.omisoft.rest.controller.BaseTestIT.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository repository;

    @Test(expected = NoSuchElementException.class)
    public void findByEmailIgnoreCaseIfEmailNull() {
        //test
        repository.findByEmailIgnoreCase(null).get();
    }

    @Test(expected = NoSuchElementException.class)
    public void findByEmailIgnoreCaseIfEmailNotExists() {
        //test
        repository.findByEmailIgnoreCase(BaseTestIT.EMAIL_NOT_EXISTS).get();
    }

    @Test
    public void findByEmailIgnoreCaseIfEmailToUpperCase() {
        //test
        UserEntity user = repository.findByEmailIgnoreCase(EMAIL_EXISTS.toUpperCase()).get();
        //validate
        assertThat(user)
                .isNotNull();
        assertThat(user.getEmail())
                .isEqualTo(EMAIL_EXISTS);
    }

    @Test
    public void findByEmail() {
        //test
        UserEntity user = repository.findByEmailIgnoreCase(EMAIL_EXISTS).get();
        //validate
        assertThat(user.getEmail())
                .isEqualTo(EMAIL_EXISTS);
    }

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