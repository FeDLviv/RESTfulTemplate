package net.omisoft.rest.service.fcm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.omisoft.rest.configuration.PropertiesConfiguration;
import net.omisoft.rest.model.base.OS;
import net.omisoft.rest.model.projection.FCMTokenProjection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

//https://peter-gribanov.github.io/serviceworker
@Service
@AllArgsConstructor
@Slf4j
public class FCMServiceImpl implements FCMService {

    public final static int MAX_TOKENS = 1000;

    private final PropertiesConfiguration propertiesConfiguration;
    private final HttpHeaders headers = new HttpHeaders();
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        headers.set("Authorization", "key=" + propertiesConfiguration.getFcm().getServerKey());
        headers.set("Content-Type", "application/json");
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
    }

    @Override
    public void send(Set<FCMTokenProjection> tokens, String title, String body, Map<String, Object> params, FCMType type) {
        if (!tokens.isEmpty()) {
            Map<OS, List<String>> map = tokens
                    .stream()
                    .collect(Collectors.groupingBy(FCMTokenProjection::getOs, Collectors.mapping(FCMTokenProjection::getToken, Collectors.toList())));
            sendIos(map.get(OS.IOS), title, body, params, type);
            sendAndroid(map.get(OS.ANDROID), title, body, params, type);
        }
    }

    @Async
    @Override
    public void sendAsync(Set<FCMTokenProjection> tokens, String title, String body, Map<String, Object> params, FCMType type) {
        send(tokens, title, body, params, type);
    }

    private void sendIos(List<String> tokens, String title, String body, Map<String, Object> params, FCMType type) {
        if (tokens != null && !tokens.isEmpty()) {
            for (List<String> list : Iterables.partition(tokens, MAX_TOKENS)) {
                String json = generateJsonIos(list, title, body, params, type.toString());
                HttpEntity<String> request = new HttpEntity<>(json, headers);
                try {
                    restTemplate.postForObject(propertiesConfiguration.getFcm().getEndpoint(), request, String.class);
                } catch (HttpClientErrorException ex) {
                    log.warn(getClass().getName(), ex);
                }
            }
        }
    }

    private void sendAndroid(List<String> tokens, String title, String body, Map<String, Object> params, FCMType type) {
        if (tokens != null && !tokens.isEmpty()) {
            for (List<String> list : Iterables.partition(tokens, MAX_TOKENS)) {
                String json = generateJsonAndroid(list, title, body, params, type.toString());
                HttpEntity<String> request = new HttpEntity<>(json, headers);
                try {
                    restTemplate.postForObject(propertiesConfiguration.getFcm().getEndpoint(), request, String.class);
                } catch (HttpClientErrorException ex) {
                    log.warn(getClass().getName(), ex);
                }
            }
        }
    }

    private String generateJsonIos(List<String> tokens, String title, String body, Map<String, Object> params, String type) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("registration_ids", tokens);
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", title);
        notification.put("body", body);
        msg.put("notification", notification);
        Map<String, Object> data = new HashMap<>();
        if (params != null) {
            params.forEach(data::putIfAbsent);
        }
        data.put("type", type);
        msg.put("data", data);
        try {
            return objectMapper.writeValueAsString(msg);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    private String generateJsonAndroid(List<String> tokens, String title, String body, Map<String, Object> params, String type) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("registration_ids", tokens);
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("body", body);
        if (params != null) {
            params.forEach(data::putIfAbsent);
        }
        data.put("type", type);
        msg.put("data", data);
        try {
            return objectMapper.writeValueAsString(msg);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

}