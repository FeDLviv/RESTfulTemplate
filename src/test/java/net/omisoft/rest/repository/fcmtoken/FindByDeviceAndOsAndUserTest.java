package net.omisoft.rest.repository.fcmtoken;

import net.omisoft.rest.model.FCMTokenEntity;
import net.omisoft.rest.model.UserEntity;
import net.omisoft.rest.model.base.OS;
import net.omisoft.rest.repository.BaseTestData;
import net.omisoft.rest.repository.FCMTokenRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

public class FindByDeviceAndOsAndUserTest extends BaseTestData {

    @Autowired
    private FCMTokenRepository repository;

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