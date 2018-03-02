package net.omisoft.rest.controller;

import lombok.AllArgsConstructor;
import net.omisoft.rest.exception.custom.CustomException;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.stream.Stream;

import static net.omisoft.rest.ApplicationConstants.PROFILE_PROD;

@RestController
@AllArgsConstructor
public class CustomErrorController implements ErrorController {

    private static final String PATH = "/error";

    private final ErrorAttributes errorAttributes;
    private final Environment environment;

    @RequestMapping(value = PATH)
    public ResponseEntity<?> error(WebRequest request, HttpServletResponse response) {
        if(Stream.of(environment.getActiveProfiles()).anyMatch(profile -> Objects.equals(profile, PROFILE_PROD))) {
            CustomException ex = new CustomException(errorAttributes.getErrorAttributes(request, false).get("error").toString());
            return ResponseEntity.status(response.getStatus()).body(ex);
        } else {
            return ResponseEntity.status(response.getStatus()).body(errorAttributes.getErrorAttributes(request, true));
        }
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }

}
