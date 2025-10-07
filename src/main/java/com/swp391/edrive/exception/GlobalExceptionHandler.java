package com.swp391.edrive.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.swp391.edrive.dto.response.ResponseObject;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    // Sai format JSON hoặc sai định dạng ngày trong body
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
    public ResponseEntity<ResponseObject> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest()
                .body(new ResponseObject(HttpStatus.BAD_REQUEST.value(), msg, null));
    }

    // Lỗi nghiệp vụ khác
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ResponseObject> handleBusiness(RuntimeException ex) {
        return ResponseEntity.badRequest()
                .body(new ResponseObject(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<ResponseObject> handleConstraint(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest()
                .body(new ResponseObject(400, msg, null));
    }
}
