package net.omisoft.rest.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import net.omisoft.rest.configuration.MessageSourceConfiguration;
import net.omisoft.rest.pojo.CustomMessage;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@AllArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final MessageSourceConfiguration message;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setHeader("Content-Type", "application/json; charset=UTF-8");
        response.getOutputStream().write(objectMapper.writeValueAsString(new CustomMessage(message.getMessage("exception.auth.required"))).getBytes());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

}
