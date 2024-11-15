package com.splanet.splanet.core.exception;

import com.splanet.splanet.core.exception.ResponseConstants;
import com.splanet.splanet.log.service.LogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  protected void handleGeneralException(HttpServletRequest request, HttpServletResponse response, Exception ex) {
    // 예외를 로그로 기록
    logService.recordErrorLog("처리되지 않은 예외 발생", ex);

    // 응답이 이미 커밋되었는지 확인
    if (!response.isCommitted()) {
      try {
        // 필요한 경우 상태 코드와 에러 메시지를 설정
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");
      } catch (IOException e) {
        // 추가로 처리할 예외가 있다면 여기서 처리
        logService.recordErrorLog("에러 응답 전송 중 오류 발생", e);
      }
    }
  }
}

