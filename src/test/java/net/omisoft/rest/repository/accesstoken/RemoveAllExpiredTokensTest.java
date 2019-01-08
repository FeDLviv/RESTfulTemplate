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

public class RemoveAllExpiredTokensTest extends BaseTestData {

    @Autowired
    private AccessTokenRepository repository;

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