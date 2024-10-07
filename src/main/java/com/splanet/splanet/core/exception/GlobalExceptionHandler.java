package com.splanet.splanet.core.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        ErrorResponse response = new ErrorResponse(errorCode.getMessage(), errorCode.getStatus().value());
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getMessage())
                .findFirst()
                .orElse("유효성 검사에 실패하였습니다.");

        ErrorResponse response = new ErrorResponse(errorMessage, 400);
        return new ResponseEntity<>(response, org.springframework.http.HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String errorMessage = ex.getMostSpecificCause().getMessage();

        // 닉네임 중복에 대한 처리
        if (errorMessage.contains("nickname")) {
            ErrorResponse response = new ErrorResponse("닉네임이 중복되었습니다.", 400);
            return new ResponseEntity<>(response, org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        ErrorResponse response = new ErrorResponse("입력값이 유효하지 않습니다.", 400);
        return new ResponseEntity<>(response, org.springframework.http.HttpStatus.BAD_REQUEST);
    }
}
