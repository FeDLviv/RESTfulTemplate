package net.omisoft.rest.controller;

import lombok.AllArgsConstructor;
import net.omisoft.rest.pojo.CustomMessage;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.stream.Stream;

import static net.omisoft.rest.ApplicationConstants.API_V1_BASE_PATH;
import static net.omisoft.rest.ApplicationConstants.PROFILE_PROD;

@Controller
@AllArgsConstructor
public class CustomErrorController implements ErrorController {

    private static final String PATH = "/error";

    private final ErrorAttributes errorAttributes;
    private final Environment environment;

    @RequestMapping(value = PATH)
    public Object error(WebRequest request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String errorRequestUri = httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI).toString();
        if (errorRequestUri.startsWith(API_V1_BASE_PATH)) {
            if (Stream.of(environment.getActiveProfiles()).anyMatch(profile -> Objects.equals(profile, PROFILE_PROD))) {
                CustomMessage ex = new CustomMessage(errorAttributes.getErrorAttributes(request, false).get("error").toString());
                return ResponseEntity.status(httpServletResponse.getStatus()).body(ex);
            } else {
                return ResponseEntity.status(httpServletResponse.getStatus()).body(errorAttributes.getErrorAttributes(request, true));
            }
        } else {
            //TODO for SPA
            return "forward:/";
        }
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }

}
