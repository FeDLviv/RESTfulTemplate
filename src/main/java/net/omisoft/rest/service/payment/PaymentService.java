package net.omisoft.rest.service.payment;

import net.omisoft.rest.model.UserEntity;
import net.omisoft.rest.service.interkassa.InterkassaUrl;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;

public interface PaymentService {

    String PAYMENT_DESCRIPTION = "Оплата за ...";

    String getCurrencySymbol();

    Map<String, String> preparedPayment(BigDecimal amount, String email, Locale locale, UserEntity currentUser);

    void updatePaymentInfo(String uuid, InterkassaUrl url);

    void updatePaymentState(String ip, String uuid, Map<String, String> data);

}