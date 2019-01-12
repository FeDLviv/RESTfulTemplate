package net.omisoft.rest.repository.payment;

import net.omisoft.rest.model.PaymentEntity;
import net.omisoft.rest.repository.BaseTestData;
import net.omisoft.rest.repository.PaymentRepository;
import org.apache.logging.log4j.util.Strings;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static net.omisoft.rest.controller.BaseTestIT.PAYMENT_UUID_EXISTS;
import static net.omisoft.rest.controller.BaseTestIT.PAYMENT_UUID_NOT_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;

public class FindByUuidTest extends BaseTestData {

    @Autowired
    private PaymentRepository repository;

    @Test
    public void findByUuidIfUuidNull() {
        //test
        boolean result = repository.findByUuid(null).isPresent();
        //validate
        assertThat(result)
                .isFalse();
    }

    @Test
    public void findByUuidIfUuidIsEmpty() {
        //test
        boolean result = repository.findByUuid(Strings.EMPTY).isPresent();
        //validate
        assertThat(result)
                .isFalse();
    }

    @Test
    public void findByUuidIfUuidNotExists() {
        //test
        boolean result = repository.findByUuid(PAYMENT_UUID_NOT_EXISTS).isPresent();
        //validate
        assertThat(result)
                .isFalse();
    }

    @Test
    public void findByUuid() {
        //test
        PaymentEntity payment = repository.findByUuid(PAYMENT_UUID_EXISTS).orElseThrow(NullPointerException::new);
        //validate
        assertThat(payment)
                .isNotNull();
        assertThat(payment.getUuid())
                .isEqualTo(PAYMENT_UUID_EXISTS);
    }

}