package com.example.ekyc_service.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class ServerException extends RuntimeException {

    /**
     * Universal version identifier for a Serializable class.
     */
    @Serial
    private static final long serialVersionUID = -6923114105537352611L;

    /**
     * This variable holds the exception message.
     */
    private final String message;

    /**
     * This variable holds the Exception status code.
     */
    private final HttpStatus httpStatus;

    public ServerException(String message, HttpStatus httpStatus) {
        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public ServerException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.message = message;
        this.httpStatus = httpStatus;
    }

}

