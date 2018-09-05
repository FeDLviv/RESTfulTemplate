package net.omisoft.rest.repositories;

import net.omisoft.rest.controllers.BaseTestIT;
import net.omisoft.rest.model.UserEntity;
import net.omisoft.rest.repository.UserRepository;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.NoSuchElementException;

import static net.omisoft.rest.controllers.BaseTestIT.EMAIL_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Test(expected = NoSuchElementException.class)
    public void findByEmailIgnoreCaseIfEmailNull() {
        repository.findByEmailIgnoreCase(null).get();
    }

    @Test(expected = NoSuchElementException.class)
    public void findByEmailIgnoreCaseIfEmailNotExists() {
        repository.findByEmailIgnoreCase(BaseTestIT.EMAIL_NOT_EXISTS).get();
    }

    @Test
    public void findByEmailIgnoreCaseIfEmailToUpperCase() {
        UserEntity user = repository.findByEmailIgnoreCase(EMAIL_EXISTS.toUpperCase()).get();
        assertThat(user)
                .isNotNull();
        assertThat(user.getEmail())
                .isEqualTo(EMAIL_EXISTS);
    }

    @Test
    public void findByEmail() {
        UserEntity user = repository.findByEmailIgnoreCase(EMAIL_EXISTS).get();
        assertThat(user.getEmail())
                .isEqualTo(EMAIL_EXISTS);
    }

}