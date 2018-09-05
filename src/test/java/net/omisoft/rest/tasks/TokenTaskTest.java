package net.omisoft.rest.tasks;

import net.omisoft.rest.repository.AccessTokenRepository;
import net.omisoft.rest.task.TokenTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class TokenTaskTest {

    @TestConfiguration
    public static class TokenTaskTestContextConfiguration {

        @MockBean
        private AccessTokenRepository accessTokenRepository;

        @Bean
        public TokenTask tokenTask() {
            return new TokenTask(accessTokenRepository);
        }

    }

    @Autowired
    private TokenTask tokenTask;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Test
    public void test() {
        tokenTask.removeExpiredTokens();
        Mockito.verify(accessTokenRepository, Mockito.times(1)).removeAllExpiredTokens();
    }

}