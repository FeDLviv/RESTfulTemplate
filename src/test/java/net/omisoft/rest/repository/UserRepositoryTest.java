package net.omisoft.rest.repository;

import net.omisoft.rest.controller.BaseTestIT;
import net.omisoft.rest.model.UserEntity;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.NoSuchElementException;

import static net.omisoft.rest.controller.BaseTestIT.EMAIL_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserRepositoryTest {

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

}