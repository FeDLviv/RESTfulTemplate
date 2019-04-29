package net.omisoft.rest.exception;

import lombok.Getter;

public class BadRequestException extends RuntimeException {

    @Getter
    private String property;

    public BadRequestException() {
        this(null, null);
    }

    public BadRequestException(String message) {
        this(message, null);
    }

    public BadRequestException(String message, String property) {
        super(message);
        this.property = property;
    }

}
