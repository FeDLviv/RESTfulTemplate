package net.omisoft.rest.repository;

import net.omisoft.rest.model.FCMTokenEntity;
import net.omisoft.rest.model.UserEntity;
import net.omisoft.rest.model.base.OS;
import net.omisoft.rest.pojo.CustomFCMToken;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FCMTokenRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FCMTokenRepository repository;

    @Test
    public void getTokenByIdUserIfUsersNull() {
        //test
        Set<CustomFCMToken> token = repository.getTokenByIdUser(null);
        //validate
        assertThat(token)
                .isNotNull()
                .isEmpty();
    }

    @Test
    public void getTokenByIdUserIfUsersEmpty() {
        //test
        Set<CustomFCMToken> token = repository.getTokenByIdUser(Collections.emptySet());
        //validate
        assertThat(token)
                .isNotNull()
                .isEmpty();
    }

    @Test
    public void getTokenByIdUserIfUserNotExists() {
        //test
        Set<CustomFCMToken> token = repository.getTokenByIdUser(Sets.newSet(666L));
        //validate
        assertThat(token)
                .isNotNull()
                .isEmpty();
    }

    @Test
    public void getTokenByIdUser() {
        //prepare
        FCMTokenEntity entity = new FCMTokenEntity();
        entity.setToken("random");
        entity.setDevice("pc");
        entity.setOs(OS.IOS);
        entity.setUser(entityManager.find(UserEntity.class, 1L));
        entityManager.persistAndFlush(entity);
        //test
        Set<CustomFCMToken> token = repository.getTokenByIdUser(Sets.newSet(1L, 2L));
        //validate
        assertThat(token)
                .isNotNull()
                .hasSize(3);
    }

    @Test(expected = NoSuchElementException.class)
    public void findByDeviceAndOsAndUserIfDeviceNull() {
        //test
        repository.findByDeviceAndOsAndUser(null, OS.ANDROID, entityManager.find(UserEntity.class, 1L)).get();
    }

    @Test(expected = NoSuchElementException.class)
    public void findByDeviceAndOsAndUserIfOsNull() {
        //test
        repository.findByDeviceAndOsAndUser("PC", null, entityManager.find(UserEntity.class, 1L)).get();
    }

    @Test(expected = NoSuchElementException.class)
    public void findByDeviceAndOsAndUserIfUserNull() {
        //test
        repository.findByDeviceAndOsAndUser("PC", OS.ANDROID, null).get();
    }

    @Test(expected = NoSuchElementException.class)
    public void findByDeviceAndOsAndUserIfUserNotExists() {
        //prepare
        UserEntity user = entityManager.find(UserEntity.class, 2L);
        entityManager.remove(user);
        entityManager.flush();
        //test
        repository.findByDeviceAndOsAndUser("PC", OS.ANDROID, user).get();
    }

    @Test
    public void findByDeviceAndOsAndUser() {
        //prepare
        UserEntity user = entityManager.find(UserEntity.class, 2L);
        //test
        FCMTokenEntity token = repository.findByDeviceAndOsAndUser("PC", OS.ANDROID, user).get();
        //validate
        assertThat(token)
                .isNotNull();
    }

}