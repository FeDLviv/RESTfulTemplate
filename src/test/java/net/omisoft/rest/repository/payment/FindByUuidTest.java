package net.omisoft.rest.repository.payment;

import net.omisoft.rest.model.PaymentEntity;
import net.omisoft.rest.repository.BaseTestData;
import net.omisoft.rest.repository.PaymentRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;

import static net.omisoft.rest.controller.BaseTestIT.PAYMENT_UUID_EXISTS;
import static net.omisoft.rest.controller.BaseTestIT.PAYMENT_UUID_NOT_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;

public class FindByUuidTest extends BaseTestData {

    @Autowired
    private PaymentRepository repository;

    @Test(expected = NoSuchElementException.class)
    public void findByUuidIfUuidNull() {
        //test
        repository.findByUuid(null).get();
    }

    @Test(expected = NoSuchElementException.class)
    public void findByUuidIfUuidNotExists() {
        //test
        repository.findByUuid(PAYMENT_UUID_NOT_EXISTS).get();
    }

    @Test
    public void findByUuid() {
        //test
        PaymentEntity payment = repository.findByUuid(PAYMENT_UUID_EXISTS).get();
        //validate
        assertThat(payment)
                .isNotNull();
        assertThat(payment.getUuid())
                .isEqualTo(PAYMENT_UUID_EXISTS);
    }

}