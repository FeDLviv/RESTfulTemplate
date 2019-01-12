package net.omisoft.rest.service.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import net.omisoft.rest.configuration.MessageSourceConfiguration;
import net.omisoft.rest.exception.BadRequestException;
import net.omisoft.rest.exception.ResourceNotFoundException;
import net.omisoft.rest.model.PaymentEntity;
import net.omisoft.rest.model.UserEntity;
import net.omisoft.rest.repository.PaymentRepository;
import net.omisoft.rest.service.interkassa.InterkassaLog;
import net.omisoft.rest.service.interkassa.InterkassaService;
import net.omisoft.rest.service.interkassa.InterkassaState;
import net.omisoft.rest.service.interkassa.InterkassaUrl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final InterkassaService interkassaService;
    private final PaymentRepository paymentRepository;
    private final MessageSourceConfiguration message;

    @Override
    public String getCurrencySymbol() {
        return interkassaService.getCurrencySymbol();
    }

    @Override
    @Transactional
    public Map<String, String> preparedPayment(BigDecimal amount, String email, Locale locale, UserEntity currentUser) {
        try {
            Map<String, String> data = interkassaService.preparedCheckout(amount, PAYMENT_DESCRIPTION, email, locale);
            paymentRepository.save(new PaymentEntity(data.get("ik_pm_no"), amount, data.get("ik_cur"), InterkassaUrl.checkout, currentUser));
            return data;
        } catch (NoSuchAlgorithmException e) {
        }
        return null;
    }

    @Override
    @Transactional
    public void updatePaymentInfo(String uuid, InterkassaUrl url) {
        PaymentEntity payment = paymentRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND.getReasonPhrase()));
        payment.setAbout(url);
        paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public void updatePaymentState(String ip, String uuid, Map<String, String> data) {
        if (Arrays.asList(InterkassaService.IP).contains(ip)) {
            try {
                if (!interkassaService.checkDigitalSignature(data)) {
                    throw new BadRequestException(message.getMessage("exception.payment.digital_signature"));
                }
            } catch (NoSuchAlgorithmException e) {
            }
            if (uuid.equals(data.get("ik_pm_no"))) {
                PaymentEntity payment = paymentRepository.findByUuid(uuid)
                        .orElseThrow(() -> new ResourceNotFoundException(message.getMessage("exception.payment.not_exists")));
                if (payment.getAmount().compareTo(new BigDecimal(data.get("ik_am"))) != 0) {
                    throw new BadRequestException(message.getMessage("exception.payment.sum"));
                }
                if (!interkassaService.getId().equals(data.get("ik_co_id"))) {
                    throw new BadRequestException(message.getMessage("exception.payment.wrong_id_office"));
                }
                payment.setState(InterkassaState.valueOf(data.get("ik_inv_st")));
                try {
                    InterkassaLog log = MAPPER.readValue(MAPPER.writeValueAsString(data), InterkassaLog.class);
                    payment.setResponse(log);
                } catch (IOException e) {
                }
                paymentRepository.save(payment);
            } else {
                throw new BadRequestException(message.getMessage("exception.payment.wrong_id"));
            }
        } else {
            throw new ResourceNotFoundException(HttpStatus.NOT_FOUND.getReasonPhrase());
        }
    }

}