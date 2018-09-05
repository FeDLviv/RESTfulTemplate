package net.omisoft.rest.exception.custom;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.omisoft.rest.configuration.MessageSourceConfiguration;
import net.omisoft.rest.exception.BadRequestException;
import net.omisoft.rest.exception.PermissionException;
import net.omisoft.rest.exception.ResourceNotFoundException;
import net.omisoft.rest.exception.UnauthorizedException;
import net.omisoft.rest.pojo.CustomMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.validation.ConstraintViolationException;
import java.lang.reflect.Field;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomExceptionHandler {

    private final MessageSourceConfiguration message;
    private final String sizeMax;

    //TODO change max file size
    @Autowired
    public CustomExceptionHandler(MessageSourceConfiguration message,
                                  @Value(value = "${spring.servlet.multipart.max-file-size}") final String sizeMax) {
        this.message = message;
        this.sizeMax = sizeMax;
    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({
            UnauthorizedException.class,
    })
    protected CustomMessage handleUnauthorizedException(UnauthorizedException ex) {
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
            BadRequestException.class,
    })
    protected CustomMessage handleBadrequestException(BadRequestException ex) {
        return new CustomMessage(ex.getMessage());
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            ResourceNotFoundException.class,
    })
    protected CustomMessage handleNotFoundException(ResourceNotFoundException ex) {
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
            String fieldName = error.getField();
            try {
                Field field = result.getTarget().getClass().getDeclaredField(fieldName);
                if (field.isAnnotationPresent(JsonProperty.class)) {
                    fieldName = field.getAnnotation(JsonProperty.class).value();
                }
            } catch (NoSuchFieldException exc) {
            }
            return new CustomMessage(fieldName + " - " + error.getDefaultMessage());
        }
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            ConstraintViolationException.class
    })
    protected CustomMessage handleViolationException(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(x -> {
                    String property = x.getPropertyPath().toString();
                    if (property.indexOf(".") != -1) {
                        property = property.substring(property.indexOf(".") + 1);
                    }
                    return property + " - " + x.getMessage();
                })
                .collect(Collectors.joining("\n"));
        return new CustomMessage(message);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            MaxUploadSizeExceededException.class
    })
    protected CustomMessage handleMaxUploadSizeException(MaxUploadSizeExceededException ex) {
        return new CustomMessage(message.getMessage("exception.max.upload.size", new Object[]{sizeMax}));
    }

}