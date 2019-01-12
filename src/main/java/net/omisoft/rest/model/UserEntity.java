package net.omisoft.rest.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.omisoft.rest.model.base.BaseEntity;
import net.omisoft.rest.model.base.UserRole;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "id_user"))
})
@EqualsAndHashCode(callSuper = true, exclude = {"payments"})
@ToString(exclude = {"payments"})
@Data
public class UserEntity extends BaseEntity {

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    public Set<PaymentEntity> payments;

}