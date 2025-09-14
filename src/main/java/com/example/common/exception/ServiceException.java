package com.example.common.exception;

import lombok.Getter;

/**
 * A custom exception to represent service-level errors.
 * This exception is used to encapsulate meaningful error messages and error codes
 * for better error handling and logging.
 */
@Getter
public class ServiceException extends RuntimeException {
    /**
     * -- GETTER --
     *  Retrieves the error code associated with this exception.
     *
     * @return the error code as a string.
     */
    private final String errorCode;

    /**
     * Constructs a new ServiceException with the specified detail message and error code.
     *
     * @param message   the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param errorCode the error code associated with this exception (for categorization or debugging purposes).
     */
    public ServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new ServiceException with the specified detail message, error code, and cause.
     *
     * @param message   the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param errorCode the error code associated with this exception (for categorization or debugging purposes).
     * @param cause     the cause (which is saved for later retrieval by the {@link #getCause()} method).
     *                  A null value is permitted and indicates that the cause is nonexistent or unknown.
     */
    public ServiceException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

}
