package net.omisoft.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;

@RestController
@AllArgsConstructor
public class CustomErrorController implements ErrorController {

    private static final String PATH = "/error";

    private final ErrorAttributes errorAttributes;

    @RequestMapping(value = PATH)
    public ResponseEntity<?> error(WebRequest request, HttpServletResponse response) {
        return ResponseEntity.status(response.getStatus()).body(errorAttributes.getErrorAttributes(request, false));
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }

}
