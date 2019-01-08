package net.omisoft.rest.repository.accesstoken;

import net.omisoft.rest.model.AccessTokenEntity;
import net.omisoft.rest.model.UserEntity;
import net.omisoft.rest.repository.AccessTokenRepository;
import net.omisoft.rest.repository.BaseTestData;
import org.apache.logging.log4j.util.Strings;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GetByTokenJoinUserTest extends BaseTestData {

    @Autowired
    private AccessTokenRepository repository;

    @Test(expected = NoSuchElementException.class)
    public void getByTokenJoinUserIfTokenNull() {
        //test
        repository.getByTokenJoinUser(null).get();
    }

    @Test(expected = NoSuchElementException.class)
    public void getByTokenJoinUserIfTokenEmpty() {
        //test
        repository.getByTokenJoinUser(Strings.EMPTY).get();
    }

    @Test(expected = NoSuchElementException.class)
    public void getByTokenJoinUserIfUserNotExists() {
        //prepare
        UserEntity user = entityManager.find(UserEntity.class, 1L);
        AccessTokenEntity accessToken = entityManager.persistAndFlush(new AccessTokenEntity(UUID.randomUUID().toString(), user, new Date()));
        entityManager.remove(user);
        entityManager.flush();
        //test
        repository.getByTokenJoinUser(accessToken.getToken()).get();
    }

    @Test
    public void getByTokenJoinUser() {
        //prepare
        UserEntity user = entityManager.find(UserEntity.class, 1L);
        AccessTokenEntity accessToken = entityManager.persistAndFlush(new AccessTokenEntity(UUID.randomUUID().toString(), user, new Date()));
        //test
        AccessTokenEntity entity = repository.getByTokenJoinUser(accessToken.getToken()).get();
        //validate
        assertThat(entity)
                .isNotNull();
        assertThat(entity.getToken())
                .isEqualTo(accessToken.getToken());
        assertThat(entity.getUser())
                .isEqualTo(user);
    }

}