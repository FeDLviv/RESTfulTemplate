package net.omisoft.rest.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.omisoft.rest.model.base.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "users")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "id_user"))
})
//TODO @EqualsAndHashCode(exclude = {"XXX"})
@EqualsAndHashCode(callSuper = true)
@Data
public class UserEntity extends BaseEntity{

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    private String role;

}
