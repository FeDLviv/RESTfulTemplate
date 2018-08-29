package net.omisoft.rest.model;

import lombok.*;
import net.omisoft.rest.model.base.BaseEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "access_tokens")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "id_access_token"))
})
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class AccessTokenEntity extends BaseEntity {

    @NonNull
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @NonNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user", nullable = false)
    private UserEntity user;

    @NonNull
    @Column(name = "expired_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expired;

}