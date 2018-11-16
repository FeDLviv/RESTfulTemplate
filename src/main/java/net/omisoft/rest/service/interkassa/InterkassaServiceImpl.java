package net.omisoft.rest.service.interkassa;

import lombok.AllArgsConstructor;
import net.omisoft.rest.configuration.PropertiesConfiguration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class InterkassaServiceImpl implements InterkassaService {

    private static final String DEFAULT_EMAIL = "default@email.com";

    private final PropertiesConfiguration propertiesConfiguration;

    @Override
    public String getCurrencySymbol() {
        return Currency.getInstance(propertiesConfiguration.getInterkassa().getCurrency()).getSymbol();
    }

    @Override
    public Map<String, String> preparedCheckout(BigDecimal amount, String description, String email, Locale locale) throws NoSuchAlgorithmException {
        SortedMap<String, String> map = new TreeMap<>();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        map.put("ik_co_id", propertiesConfiguration.getInterkassa().getId());
        map.put("ik_cur", propertiesConfiguration.getInterkassa().getCurrency());
        map.put("ik_pm_no", uuid);
        map.put("ik_am", amount.toString());
        map.put("ik_desc", description);
        if (email == null || email.trim().length() == 0) {
            email = DEFAULT_EMAIL;
        }
        map.put("ik_cli", email);
        map.put("ik_loc", locale == null ? LocaleContextHolder.getLocale().getLanguage() : locale.getLanguage());
        map.put("ik_suc_u", ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString() + "/" + InterkassaUrl.success + "/" + uuid);
        map.put("ik_suc_m", "post");
        map.put("ik_fal_u", ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString() + "/" + InterkassaUrl.fail + "/" + uuid);
        map.put("ik_fal_m", "post");
        map.put("ik_pnd_u", ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString() + "/" + InterkassaUrl.pending + "/" + uuid);
        map.put("ik_pnd_m", "post");
        map.put("ik_ia_u", ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString() + "/" + InterkassaUrl.interaction + "/" + uuid);
        map.put("ik_ia_m", "post");
        String result = map.values().stream().collect(Collectors.joining(":"));
        result += ":" + propertiesConfiguration.getInterkassa().getKey();
        byte[] digest = MessageDigest.getInstance("MD5").digest(result.getBytes());
        map.put("ik_sign", Base64.getEncoder().encodeToString(digest));
        return map;
    }

    @Override
    public boolean checkDigitalSignature(Map<String, String> data) throws NoSuchAlgorithmException {
        SortedMap<String, String> map = new TreeMap(data);
        map.remove("ik_sign");
        String result = map.values().stream().collect(Collectors.joining(":"));
        String key = propertiesConfiguration.getInterkassa().getTestKey().equals("-1") ? propertiesConfiguration.getInterkassa().getKey() : propertiesConfiguration.getInterkassa().getTestKey();
        result += ":" + key;
        byte[] digest = MessageDigest.getInstance("MD5").digest(result.getBytes());
        return Base64.getEncoder().encodeToString(digest).equals(data.get("ik_sign"));
    }

}