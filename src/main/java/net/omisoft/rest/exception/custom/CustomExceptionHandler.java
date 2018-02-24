package net.omisoft.rest.exception.custom;

import net.omisoft.rest.exception.PermissionException;
import net.omisoft.rest.exception.ResourceNotFoundException;
import net.omisoft.rest.util.MessageByLocaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    @Autowired
    private MessageByLocaleService message;

    @ExceptionHandler({
            ResourceNotFoundException.class,
    })
    protected ResponseEntity<CustomException> handleNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(new CustomException(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            PermissionException.class,
    })
    protected ResponseEntity<CustomException> handleForbiddenException(PermissionException ex) {
        return new ResponseEntity<>(new CustomException(ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class
    })
    protected ResponseEntity<CustomException> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        FieldError error = result.getFieldError();
        try {
            String msg = message.getMessage(error.getDefaultMessage());
            return new ResponseEntity<>(new CustomException(msg), HttpStatus.BAD_REQUEST);
        } catch (NoSuchMessageException e) {
            return new ResponseEntity<>(new CustomException(error.getField() + " - " + error.getDefaultMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}

