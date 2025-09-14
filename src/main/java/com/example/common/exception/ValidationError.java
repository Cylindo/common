package com.example.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a validation error for a specific field.
 */
@Data
@AllArgsConstructor
public class ValidationError {
    /**
     * The name of the field where the validation error occurred.
     */
    private String field;

    /**
     * A human-readable message describing the validation error.
     */
    private String message;

    /**
     * The JSON path to the problematic field, useful for nested structures.
     */
    private String path;
}