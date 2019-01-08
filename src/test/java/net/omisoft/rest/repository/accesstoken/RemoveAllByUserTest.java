package net.omisoft.rest.repository.accesstoken;

import net.omisoft.rest.model.AccessTokenEntity;
import net.omisoft.rest.model.UserEntity;
import net.omisoft.rest.repository.AccessTokenRepository;
import net.omisoft.rest.repository.BaseTestData;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class RemoveAllByUserTest extends BaseTestData {

    @Autowired
    private AccessTokenRepository repository;

    @Test
    public void removeAllByUserIfUserNull() {
        //prepare
        entityManager.persistAndFlush(new AccessTokenEntity(UUID.randomUUID().toString(), entityManager.find(UserEntity.class, 1L), new Date()));
        long before = repository.count();
        //test
        repository.removeAllByUser(null);
        //validate
        long after = repository.count();
        assertThat(after)
                .isEqualTo(before)
                .isEqualTo(1);
    }

    @Test
    public void removeAllByUserIfUserNotExists() {
        //prepare
        UserEntity user = entityManager.find(UserEntity.class, 1L);
        entityManager.persistAndFlush(new AccessTokenEntity(UUID.randomUUID().toString(), user, new Date()));
        entityManager.remove(user);
        entityManager.flush();
        long before = repository.count();
        //test
        repository.removeAllByUser(user);
        //validate
        long after = repository.count();
        assertThat(after)
                .isEqualTo(before)
                .isEqualTo(0);
    }

    @Test
    public void removeAllByUser() {
        //prepare
        UserEntity user = entityManager.find(UserEntity.class, 1L);
        for (int i = 0; i < 2; i++) {
            entityManager.persist(new AccessTokenEntity(UUID.randomUUID().toString(), user, new Date()));
        }
        entityManager.persist(new AccessTokenEntity(UUID.randomUUID().toString(), entityManager.find(UserEntity.class, 2L), new Date()));
        entityManager.flush();
        long before = repository.count();
        //test
        repository.removeAllByUser(user);
        //validate
        long after = repository.count();
        assertThat(after)
                .isNotEqualTo(before)
                .isEqualTo(1);
    }

}