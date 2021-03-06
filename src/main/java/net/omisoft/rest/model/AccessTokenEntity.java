package net.omisoft.rest.model;

import lombok.*;
import net.omisoft.rest.model.base.BaseEntity;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.util.Date;

@Entity
@Immutable
@Table(name = "access_tokens")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "id_access_token"))
})
@EqualsAndHashCode(callSuper = true, exclude = {"user"})
@ToString(exclude = {"user"})
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class AccessTokenEntity extends BaseEntity {

    @NonNull
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @NonNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_user", nullable = false)
    private UserEntity user;

    @NonNull
    @Column(name = "expired_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expired;

}