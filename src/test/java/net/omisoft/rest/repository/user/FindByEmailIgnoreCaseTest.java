package net.omisoft.rest.repository.user;

import net.omisoft.rest.controller.BaseTestIT;
import net.omisoft.rest.model.UserEntity;
import net.omisoft.rest.repository.BaseTestData;
import net.omisoft.rest.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;

import static net.omisoft.rest.controller.BaseTestIT.EMAIL_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;

public class FindByEmailIgnoreCaseTest extends BaseTestData {

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
    public void findByEmailIgnoreCase() {
        //test
        UserEntity user = repository.findByEmailIgnoreCase(EMAIL_EXISTS).get();
        //validate
        assertThat(user.getEmail())
                .isEqualTo(EMAIL_EXISTS);
    }

}