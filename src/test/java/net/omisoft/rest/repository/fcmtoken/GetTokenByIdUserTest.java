package net.omisoft.rest.repository.fcmtoken;

import net.omisoft.rest.model.FCMTokenEntity;
import net.omisoft.rest.model.UserEntity;
import net.omisoft.rest.model.base.OS;
import net.omisoft.rest.model.projection.FCMTokenProjection;
import net.omisoft.rest.repository.BaseTestData;
import net.omisoft.rest.repository.FCMTokenRepository;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class GetTokenByIdUserTest extends BaseTestData {

    @Autowired
    private FCMTokenRepository repository;

    @Test
    public void getTokenByIdUserIfUsersNull() {
        //test
        Set<FCMTokenProjection> token = repository.getTokenByIdUser(null);
        //validate
        assertThat(token)
                .isNotNull()
                .isEmpty();
    }

    @Test
    public void getTokenByIdUserIfUsersEmpty() {
        //test
        Set<FCMTokenProjection> token = repository.getTokenByIdUser(Collections.emptySet());
        //validate
        assertThat(token)
                .isNotNull()
                .isEmpty();
    }

    @Test
    public void getTokenByIdUserIfUserNotExists() {
        //test
        Set<FCMTokenProjection> token = repository.getTokenByIdUser(Sets.newSet(666L));
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
        Set<FCMTokenProjection> token = repository.getTokenByIdUser(Sets.newSet(1L, 2L));
        //validate
        assertThat(token)
                .isNotNull()
                .hasSize(3);
    }

}