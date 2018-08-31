package net.omisoft.rest.repository;

import net.omisoft.rest.model.AccessTokenEntity;
import net.omisoft.rest.model.UserEntity;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccessTokenRepository extends CrudRepository<AccessTokenEntity, Long> {

    @Cacheable(value = "tokens", key = "#p0", condition = "#p0!=null", unless = "#result==null")
    @Query("SELECT t FROM AccessTokenEntity t WHERE t.token=?1")
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"user"})
    Optional<AccessTokenEntity> getByTokenJoinUser(String token);

    @CacheEvict(value = "tokens", allEntries = true)
    @Modifying
    @Query("DELETE FROM AccessTokenEntity t WHERE t.user = :user")
    void removeAllByUser(@Param("user") UserEntity user);

    @CacheEvict(value = "tokens", allEntries = true)
    @Modifying
    @Query("DELETE FROM AccessTokenEntity t WHERE t.expired < CURRENT_TIMESTAMP ")
    void removeAllExpiredTokens();

}