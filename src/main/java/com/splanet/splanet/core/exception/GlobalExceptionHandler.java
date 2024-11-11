package com.splanet.splanet.core.exception;

import com.splanet.splanet.core.exception.ResponseConstants;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        String errorMessage = ex.getMessage() != null ? ex.getMessage() : "알 수 없는 오류가 발생했습니다.";
        ErrorResponse errorResponse = new ErrorResponse(errorMessage, ex.getErrorCode().getStatus().value());
        return new ResponseEntity<>(errorResponse, ex.getErrorCode().getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getMessage())
                .findFirst()
                .orElse(ResponseConstants.ErrorMessage.VALIDATION_ERROR);

        ErrorResponse response = new ErrorResponse(errorMessage, ResponseConstants.StatusCode.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String errorMessage = ex.getMostSpecificCause().getMessage();

        if (errorMessage.contains("nickname")) {
            ErrorResponse response = new ErrorResponse(ResponseConstants.ErrorMessage.DUPLICATE_NICKNAME, ResponseConstants.StatusCode.BAD_REQUEST);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        ErrorResponse response = new ErrorResponse(ResponseConstants.ErrorMessage.INVALID_INPUT, ResponseConstants.StatusCode.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
