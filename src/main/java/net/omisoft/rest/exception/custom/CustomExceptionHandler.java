package net.omisoft.rest.exception.custom;

import net.omisoft.rest.exception.PermissionException;
import net.omisoft.rest.exception.ResourceNotFoundException;
import net.omisoft.rest.pojo.CustomMessage;
import net.omisoft.rest.util.MessageByLocaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    @Autowired
    private MessageByLocaleService message;

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            ResourceNotFoundException.class,
    })
    protected CustomMessage handleNotFoundException(ResourceNotFoundException ex) {
        return new CustomMessage(ex.getMessage());
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    @ExceptionHandler({
            PermissionException.class,
    })
    protected CustomMessage handleForbiddenException(PermissionException ex) {
        return new CustomMessage(ex.getMessage());
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            MethodArgumentNotValidException.class
    })
    protected CustomMessage handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        FieldError error = result.getFieldError();
        try {
            String msg = message.getMessage(error.getDefaultMessage());
            return new CustomMessage(msg);
        } catch (NoSuchMessageException e) {
            return new CustomMessage(error.getField() + " - " + error.getDefaultMessage());
        }
    }

}

