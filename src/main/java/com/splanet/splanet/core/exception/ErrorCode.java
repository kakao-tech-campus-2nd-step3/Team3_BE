package com.splanet.splanet.core.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    ACCESS_DENIED("권한이 없습니다.", HttpStatus.FORBIDDEN),
    UNAUTHORIZED("인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED),
    ALREADY_CANCELED("이미 취소된 구독입니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND("활성화된 구독을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
