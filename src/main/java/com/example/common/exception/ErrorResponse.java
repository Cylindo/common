package com.example.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A class representing an error response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * The message describing the error.
     */
    private String message;

    /**
     * Additional details about the error.
     */
    private String details;

    /**
     * List of validation errors at the attribute or class level.
     */
    private List<ValidationError> errors;
}

