package net.omisoft.rest.service.interkassa;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Map;

public interface InterkassaService {

    String[] IP = {"151.80.190.97", "151.80.190.107", "35.233.69.55"};

    String getCurrencySymbol();

    Map<String, String> preparedCheckout(BigDecimal amount, String description, String email, Locale locale) throws NoSuchAlgorithmException;

    boolean checkDigitalSignature(Map<String, String> data) throws NoSuchAlgorithmException;

}