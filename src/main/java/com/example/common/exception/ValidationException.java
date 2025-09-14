package com.example.common.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException  extends RuntimeException {
    /**
     * -- GETTER --
     *  Returns the list of validation errors.
     *
     * @return the list of validation errors
     */
    private List<ValidationError> errors;

    /**
     * Constructs a new BusinessValidationException with the specified detail message.
     *
     * @param message the detail message
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new BusinessValidationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new BusinessValidationException with the specified detail message and validation errors.
     *
     * @param message the detail message
     * @param errors the list of validation errors
     */
    public ValidationException(String message, List<ValidationError> errors) {
        super(message);
        this.errors = errors;
    }

}
