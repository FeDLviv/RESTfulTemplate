package net.omisoft.rest.repositories;

import net.omisoft.rest.model.AccessTokenEntity;
import net.omisoft.rest.model.UserEntity;
import net.omisoft.rest.repository.AccessTokenRepository;
import org.apache.logging.log4j.util.Strings;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccessTokenRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

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

    @Test
    public void removeAllExpiredTokens() {
        //prepare
        Date[] dates = {new Date(), new Date(new Date().getTime() - 6000), new Date(new Date().getTime() + 6000)};
        UserEntity user = entityManager.find(UserEntity.class, 1L);
        for (Date date : dates) {
            entityManager.persist(new AccessTokenEntity(UUID.randomUUID().toString(), user, date));
        }
        entityManager.flush();
        long before = repository.count();
        //test
        repository.removeAllExpiredTokens();
        //validate
        long after = repository.count();
        assertThat(after)
                .isNotEqualTo(before)
                .isEqualTo(1);
    }

}