package net.omisoft.rest.repository;

import net.omisoft.rest.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmailIgnoreCase(String email);

    @Modifying
    @Query("UPDATE UserEntity u SET u.password = :#{#user.password} WHERE u.id = :#{#user.id}")
    int updatePassword(@Param("user") UserEntity user);

}