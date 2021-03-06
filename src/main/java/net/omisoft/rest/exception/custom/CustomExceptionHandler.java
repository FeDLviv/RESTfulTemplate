package net.omisoft.rest.exception.custom;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import net.omisoft.rest.configuration.MessageSourceConfiguration;
import net.omisoft.rest.exception.BadRequestException;
import net.omisoft.rest.exception.PermissionException;
import net.omisoft.rest.exception.ResourceNotFoundException;
import net.omisoft.rest.exception.UnauthorizedException;
import net.omisoft.rest.pojo.CustomMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.Field;

@RestControllerAdvice
public class CustomExceptionHandler {

    private final MessageSourceConfiguration message;
    private final String maxFileSize;
    private final String maxRequestSize;

    //TODO change max file size and max request size
    public CustomExceptionHandler(MessageSourceConfiguration message,
                                  @Value(value = "${spring.servlet.multipart.max-file-size}") String maxFileSize,
                                  @Value(value = "${spring.servlet.multipart.max-request-size}") String maxRequestSize) {
        this.message = message;
        this.maxFileSize = maxFileSize;
        this.maxRequestSize = maxRequestSize;
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
        return new CustomMessage(ex.getMessage(), ex.getProperty());
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
            MethodArgumentNotValidException.class,
            BindException.class
    })
    protected CustomMessage handleValidationException(Exception ex) throws Exception {
        BindingResult result;
        switch (ex.getClass().getSimpleName()) {
            case "MethodArgumentNotValidException":
                result = ((MethodArgumentNotValidException) ex).getBindingResult();
                break;
            case "BindException":
                result = ((BindException) ex).getBindingResult();
                break;
            default:
                throw ex;
        }
        FieldError fieldError = result.getFieldError();
        if (fieldError != null) {
            String msg = message.getMessage(fieldError.getDefaultMessage());
            String propertyName = fieldError.getField();
            try {
                Field field = result.getTarget().getClass().getDeclaredField(propertyName);
                if (field.isAnnotationPresent(JsonProperty.class)) {
                    propertyName = field.getAnnotation(JsonProperty.class).value();
                }
            } catch (NoSuchFieldException exc) {
            }
            return new CustomMessage(msg, propertyName);
        } else {
            ObjectError objectError = result.getAllErrors().stream()
                    .findFirst()
                    .get();
            return new CustomMessage(objectError.getDefaultMessage());
        }
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            ConstraintViolationException.class
    })
    protected CustomMessage handleViolationException(ConstraintViolationException ex) {
        ConstraintViolation<?> constraintViolation = ex.getConstraintViolations().stream()
                .findFirst()
                .get();
        String message = constraintViolation.getMessage();
        String property = constraintViolation.getPropertyPath().toString();
        if (property.contains(".")) {
            property = property.substring(property.indexOf(".") + 1);
        }
        return new CustomMessage(message, property);
    }

    @ResponseStatus(value = HttpStatus.PAYLOAD_TOO_LARGE)
    @ExceptionHandler({
            MaxUploadSizeExceededException.class
    })
    protected CustomMessage handleMaxUploadSizeException(MaxUploadSizeExceededException ex) {
        return new CustomMessage(message.getMessage("exception.max.upload.size", new Object[]{maxFileSize, maxRequestSize}));
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            JsonParseException.class,
    })
    protected CustomMessage handleJsonParseException(JsonParseException ex) {
        return new CustomMessage(ex.getMessage());
    }

}