package net.omisoft.rest.model;

import lombok.*;
import net.omisoft.rest.model.base.BaseEntity;
import net.omisoft.rest.model.converter.MoneyConverter;
import net.omisoft.rest.service.interkassa.InterkassaLog;
import net.omisoft.rest.service.interkassa.InterkassaState;
import net.omisoft.rest.service.interkassa.InterkassaUrl;
import org.hibernate.annotations.ColumnTransformer;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "id_payment"))
})
@EqualsAndHashCode(callSuper = true, exclude = {"user"})
@ToString(exclude = {"user"})
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class PaymentEntity extends BaseEntity {

    @NonNull
    @Column(name = "uuid", nullable = false, unique = true)
    private String uuid;

    @NonNull
    @Column(name = "amount", nullable = false)
    @Convert(converter = MoneyConverter.class)
    @ColumnTransformer(forColumn = "amount", read = "amount / 100.00", write = "? * 100")
    private BigDecimal amount;

    @NonNull
    @Column(name = "currency", nullable = false)
    private String currency;

    @NonNull
    @Enumerated(value = EnumType.STRING)
    @Column(name = "about", nullable = false)
    private InterkassaUrl about;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "state", nullable = true)
    private InterkassaState state;

    @Column(name = "log", nullable = true)
    @ColumnTransformer(forColumn = "log", write = "CAST(? AS jsonb)")
    private InterkassaLog response;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user", nullable = false)
    private UserEntity user;

}