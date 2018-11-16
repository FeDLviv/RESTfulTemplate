package net.omisoft.rest.repository;

import net.omisoft.rest.model.PaymentEntity;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.NoSuchElementException;

import static net.omisoft.rest.controller.BaseTestIT.PAYMENT_UUID_EXISTS;
import static net.omisoft.rest.controller.BaseTestIT.PAYMENT_UUID_NOT_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PaymentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

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