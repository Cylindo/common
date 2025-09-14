package com.example.common.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sentry.Sentry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.xml.transform.TransformerException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for the application.
 * This class handles various exceptions thrown by the application and provides appropriate responses.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle global exceptions (unhandled exceptions).
     *
     * @param ex      the Exception
     * @param request the WebRequest
     * @return the ResponseEntity containing ErrorResponse
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        // Log unhandled exceptions
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        Sentry.captureException(ex); // manually report internal server errors to Sentry
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), request.getDescription(false), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle ServiceException (handled exception).
     *
     * @param ex      the ServiceException
     * @param request the WebRequest
     * @return the ResponseEntity containing ErrorResponse
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException ex, WebRequest request) {
        //ServiceException is a handled exception and will be logged at the service level
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                request.getDescription(false),
                null
        );
        Sentry.captureException(ex); // manually report internal server errors to Sentry
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle TransformerException (handled exception).
     *
     * @param ex      the TransformerException
     * @param request the WebRequest
     * @return the ResponseEntity containing ErrorResponse
     */
    @ExceptionHandler(TransformerException.class)
    public ResponseEntity<ErrorResponse> handleTransformerException(TransformerException ex, WebRequest request) {
        // Log transformer-related exceptions as warnings
        log.warn("Transformer exception: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                request.getDescription(false),
                null
        );
        Sentry.captureException(ex); // manually report internal server errors to Sentry
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle ResourceNotFoundException (handled exception).
     *
     * @param ex      the ResourceNotFoundException
     * @param request the WebRequest
     * @return the ResponseEntity containing ErrorResponse
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        // No logging here for handled exceptions
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), request.getDescription(false), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle validation errors (handled exception).
     *
     * @param ex      the ValidationException
     * @param request the WebRequest
     * @return the ResponseEntity containing ErrorResponse
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex, WebRequest request) {
        // No logging here for handled exceptions
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                request.getDescription(false),
                ex.getErrors()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Handle DuplicateException (handled exception).
     *
     * @param ex      the DuplicateException
     * @param request the WebRequest
     * @return the ResponseEntity containing ErrorResponse
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateException(DuplicateResourceException ex, WebRequest request) {
        // No logging here for handled exceptions
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                request.getDescription(false),
                null
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handle IllegalArgumentException (handled exception).
     *
     * @param ex      the IllegalArgumentException
     * @param request the WebRequest
     * @return the ResponseEntity containing ErrorResponse
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        // Log invalid arguments as warnings along with the request description
        log.warn("Illegal argument: {} | Request: {}", ex.getMessage(), request.getDescription(true));

        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), request.getDescription(false), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle HttpRequestMethodNotSupportedException (handled exception).
     *
     * @param ex      the HttpRequestMethodNotSupportedException
     * @param request the WebRequest
     * @return the ResponseEntity containing ErrorResponse
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        // Log unsupported HTTP methods as warnings
        log.warn("Unsupported HTTP method: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), request.getDescription(false), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Handle HttpMediaTypeNotSupportedException (handled exception).
     *
     * @param ex      the HttpMediaTypeNotSupportedException
     * @param request the WebRequest
     * @return the ResponseEntity containing ErrorResponse
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, WebRequest request) {
        // Log unsupported media types as warnings
        log.warn("Unsupported media type: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), request.getDescription(false), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * Handle HttpMessageNotReadableException (handled exception).
     *
     * @param ex      the HttpMessageNotReadableException
     * @param request the WebRequest
     * @return the ResponseEntity containing ErrorResponse
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        // Log unreadable HTTP messages as warnings along with the request description and failed message
        String failedRequestBody = null;
        if (request instanceof ServletWebRequest servletWebRequest) {
            HttpServletRequest httpServletRequest = servletWebRequest.getRequest();
            try {
                failedRequestBody = new String(httpServletRequest.getInputStream().readAllBytes());
            } catch (Exception e) {
                log.warn("Failed to read client request body for debugging", e);
            }
        }
        log.warn("HTTP message not readable: {} | Request: {} | Failed message: {} | Client Request Body: {}",
                ex.getMessage(),
                request.getDescription(true),
                ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : "Unknown",
                failedRequestBody != null ? failedRequestBody : "Unavailable");

        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), request.getDescription(false), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle ConstraintViolationException (handled exception).
     *
     * @param ex      the ConstraintViolationException
     * @param request the WebRequest
     * @return the ResponseEntity containing ErrorResponse
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        // Log constraint violations as warnings along with the request description
        log.warn("Constraint violation: {} | Request: {}", ex.getMessage(), request.getDescription(true));

        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), request.getDescription(false), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle bean validation errors at the controller level.
     *
     * @param ex the MethodArgumentNotValidException
     * @return the ResponseEntity containing ErrorResponse
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidExceptions(MethodArgumentNotValidException ex) {
        // Log validation failures as warnings along with the request description
        log.warn("Validation failed: {} | Request: {}", ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", ")), ex.getBindingResult());

        List<ValidationError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    String fieldPath = error.getField(); // e.g., "address.street"
                    String jsonPath = resolveJsonPath(ex.getBindingResult().getTarget(), fieldPath);
                    String fieldName = jsonPath.contains(".") ? jsonPath.substring(jsonPath.lastIndexOf('.') + 1) : jsonPath;
                    return new ValidationError(fieldName, error.getDefaultMessage(), jsonPath);
                })
                .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse(
                "Validation failed.",
                "One or more fields have validation errors",
                errors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Helper method to resolve the full JSON path for nested fields
    private String resolveJsonPath(Object target, String fieldPath) {
        if (target == null || fieldPath == null) return fieldPath;
        String[] parts = fieldPath.split("\\.");
        Class<?> currentClass = target.getClass();
        StringBuilder jsonPath = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String javaField = parts[i];
            String jsonField = javaField;
            try {
                Field field = currentClass.getDeclaredField(javaField);
                JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
                if (jsonProperty != null && !jsonProperty.value().isEmpty()) {
                    jsonField = jsonProperty.value();
                }
                currentClass = field.getType();
            } catch (NoSuchFieldException e) {
                // If field not found, just use the Java field name
            }
            if (i > 0) jsonPath.append(".");
            jsonPath.append(jsonField);
        }
        return jsonPath.toString();
    }
}
