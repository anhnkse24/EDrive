package com.swp391.edrive.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.swp391.edrive.dto.response.ResponseObject;
import com.swp391.edrive.exception.exceptions.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.lang.IllegalArgumentException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseObject> handleJsonParse(HttpMessageNotReadableException ex) {
        String message = "Dữ liệu không hợp lệ.";
        if (ex.getCause() instanceof InvalidFormatException ife) {
            if (ife.getTargetType() == LocalDate.class) {
                message = "Ngày phải theo định dạng yyyy-MM-dd (ví dụ: 2025-10-03).";
            }
        }
        return ResponseEntity.badRequest()
                .body(new ResponseObject(HttpStatus.BAD_REQUEST.value(), message, null));
    }

    // Sai định dạng khi parse @RequestParam
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseObject> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        if (ex.getRequiredType() == LocalDate.class && "date".equals(ex.getName())) {
            return ResponseEntity.badRequest()
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.value(),
                            "Phải theo định dạng yyyy-MM-dd (ví dụ: 2025-10-03).", null));
        }
        return ResponseEntity.badRequest()
                .body(new ResponseObject(HttpStatus.BAD_REQUEST.value(), "Tham số không hợp lệ.", null));
    }

    // Lỗi validate @Valid (DTO)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseObject> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errorMessages = new ArrayList<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errorMessages.add(error.getField() + ": " + error.getDefaultMessage()));

        String combinedMessage = String.join(", ", errorMessages);

        return ResponseEntity.badRequest()
                .body(new ResponseObject(
                        HttpStatus.BAD_REQUEST.value(),
                        combinedMessage,
                        null
                ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseObject> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errorMessages = new ArrayList<>();

        ex.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            errorMessages.add(field + ": " + violation.getMessage());
        });

        String combinedMessage = String.join(", ", errorMessages);

        return ResponseEntity.badRequest()
                .body(new ResponseObject(HttpStatus.BAD_REQUEST.value(), combinedMessage, null));
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ResponseObject> handleDuplicate(SQLIntegrityConstraintViolationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseObject.builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message(exception.getMessage())
                        .data(null)
                        .build());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ResponseObject> handleNullPointer(NullPointerException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseObject.builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message(exception.getMessage())
                        .data(null)
                        .build());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ResponseObject> handleUnauthorizedException(UnauthorizedException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseObject.builder()
                        .statusCode(HttpStatus.UNAUTHORIZED.value())
                        .message(exception.getMessage())
                        .data(null)
                        .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseObject> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ResponseObject(
                        HttpStatus.FORBIDDEN.value(),
                        "Access Denied",
                        null
                ));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseObject> handleRuntimeExceptionException(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseObject.builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message(exception.getMessage())
                        .data(null)
                        .build());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseObject> handleNotFoundException(NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseObject.builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message(exception.getMessage())
                        .data(null)
                        .build());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ResponseObject> handleConflictException(ConflictException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResponseObject.builder()
                        .statusCode(HttpStatus.CONFLICT.value())
                        .message(exception.getMessage())
                        .data(null)
                        .build());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseObject> handleBadRequestException(BadRequestException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseObject.builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message(exception.getMessage())
                        .data(null)
                        .build());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ResponseObject> handleForbiddenException(ForbiddenException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseObject.builder()
                        .statusCode(HttpStatus.FORBIDDEN.value())
                        .message(exception.getMessage())
                        .data(null)
                        .build());
    }

    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ResponseObject> handleTokenRefreshException(TokenRefreshException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseObject.builder()
                        .statusCode(HttpStatus.FORBIDDEN.value())
                        .message(exception.getMessage())
                        .data(null)
                        .build());
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ResponseObject> handleTokenRefreshException(InternalServerErrorException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseObject.builder()
                        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message(exception.getMessage())
                        .data(null)
                        .build());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseObject> handleResourceNotFoundException(ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseObject.builder()
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message(exception.getMessage())
                        .data(null)
                        .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseObject> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseObject.builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message(exception.getMessage())
                        .data(null)
                        .build());
    }
}
