package net.omisoft.rest.repository.user;

import net.omisoft.rest.controller.BaseTestIT;
import net.omisoft.rest.repository.BaseTestData;
import net.omisoft.rest.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static net.omisoft.rest.controller.BaseTestIT.EMAIL_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;

public class ExistsByEmailIgnoreCaseIfEmailNullTest extends BaseTestData {

    @Autowired
    private UserRepository repository;

    @Test
    public void existsByEmailIgnoreCaseIfEmailNull() {
        //test
        boolean result = repository.existsByEmailIgnoreCase(null);
        //validate
        assertThat(result)
                .isFalse();
    }

    @Test
    public void existsByEmailIgnoreCaseIfEmailNotExists() {
        //test
        boolean result = repository.existsByEmailIgnoreCase(BaseTestIT.EMAIL_NOT_EXISTS);
        //validate
        assertThat(result)
                .isFalse();
    }

    @Test
    public void existsByEmailIgnoreCaseIfEmailToUpperCase() {
        //test
        boolean result = repository.existsByEmailIgnoreCase(EMAIL_EXISTS.toUpperCase());
        //validate
        assertThat(result)
                .isTrue();
    }

    @Test
    public void existsByEmailIgnoreCase() {
        //test
        boolean result = repository.existsByEmailIgnoreCase(EMAIL_EXISTS);
        //validate
        assertThat(result)
                .isTrue();
    }

}