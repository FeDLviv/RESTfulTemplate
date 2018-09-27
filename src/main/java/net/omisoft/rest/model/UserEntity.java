package net.omisoft.rest.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.omisoft.rest.model.base.BaseEntity;
import net.omisoft.rest.model.base.UserRole;

import javax.persistence.*;

@Entity
@Table(name = "users")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "id_user"))
})
//TODO @EqualsAndHashCode(exclude = {"XXX"})
@EqualsAndHashCode(callSuper = true)
//TODO @ToString(exclude = {"XXX"})
@Data
public class UserEntity extends BaseEntity {

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

}