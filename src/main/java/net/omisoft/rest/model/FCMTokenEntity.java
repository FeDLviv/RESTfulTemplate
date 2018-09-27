package net.omisoft.rest.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.omisoft.rest.model.base.BaseEntity;
import net.omisoft.rest.model.base.OS;

import javax.persistence.*;

@Entity
@Table(name = "fcm_tokens",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"device", "device_os", "id_user"})
        }
)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "id_fcm_token"))
})
@EqualsAndHashCode(callSuper = true, exclude = {"user"})
@ToString(exclude = {"user"})
@Data
public class FCMTokenEntity extends BaseEntity {

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "device", nullable = false)
    private String device;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "device_os", nullable = false)
    private OS os;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user", nullable = false)
    private UserEntity user;

}