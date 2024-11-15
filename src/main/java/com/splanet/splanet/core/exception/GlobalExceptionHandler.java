package com.splanet.splanet.core.exception;

import com.splanet.splanet.core.exception.ResponseConstants;
import com.splanet.splanet.log.service.LogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private final LogService logService;

  // LogService를 생성자 주입 받습니다.
  public GlobalExceptionHandler(LogService logService) {
    this.logService = logService;
  }


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
  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ErrorResponse> handleGeneralException(HttpServletRequest request, Exception ex) {
    // 예외를 로그로 기록
    logService.recordErrorLog("처리되지 않은 예외 발생", ex);

    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    String errorMessage = "서버 내부 오류가 발생했습니다.";

    if (ex instanceof MissingServletRequestParameterException) {
      status = HttpStatus.BAD_REQUEST;
      errorMessage = "필수 파라미터가 누락되었습니다.";
    } else if (ex instanceof MethodArgumentNotValidException) {
      status = HttpStatus.BAD_REQUEST;
      errorMessage = "요청 파라미터가 유효하지 않습니다.";
    }
    // 필요한 다른 예외 처리 추가

    ErrorResponse response = new ErrorResponse(errorMessage, status.value());
    return new ResponseEntity<>(response, status);
  }
}

