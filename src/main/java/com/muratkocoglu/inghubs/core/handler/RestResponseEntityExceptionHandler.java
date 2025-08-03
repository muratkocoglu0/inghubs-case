package com.muratkocoglu.inghubs.core.handler;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.muratkocoglu.inghubs.core.dto.ErrorCode;
import com.muratkocoglu.inghubs.core.dto.RestResponse;
import com.muratkocoglu.inghubs.core.exception.BusinessException;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class RestResponseEntityExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleDefaultException(final BusinessException ex, final WebRequest request) {
        log.error("DefaultException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        		.body(RestResponse.of(null, ErrorCode.BUSINESS_ERROR.getCode(), ex.getMessage()));
    }

    @ExceptionHandler({EntityNotFoundException.class, NoSuchElementException.class, NoResourceFoundException.class})
    public ResponseEntity<RestResponse<Void>> handleNotFound(Exception ex) {
        log.error("Entity not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(RestResponse.of(null,ErrorCode.ENTITY_NOT_FOUND.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RestResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        log.error("AccessDenied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(RestResponse.of(null, ErrorCode.ACCESS_DENIED.getCode(), "Access is denied"));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Void>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
    	log.error("DefaultException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        		.body(RestResponse.of(null, ErrorCode.BUSINESS_ERROR.getCode(), ex.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<RestResponse<Void>> handleRuntime(RuntimeException ex) {
        log.error("RuntimeException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RestResponse.of(null, ErrorCode.BUSINESS_ERROR.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(final Exception ex, final WebRequest request) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        		.body(RestResponse.of(null, ErrorCode.INTERNAL_ERROR.getCode(), "Internal server error."));
    }
}
