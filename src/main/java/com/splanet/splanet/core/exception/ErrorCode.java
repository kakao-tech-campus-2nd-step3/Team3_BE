package com.splanet.splanet.core.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    ACCESS_DENIED("권한이 없습니다.", HttpStatus.FORBIDDEN),
    UNAUTHORIZED("인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED),
    ALREADY_CANCELED("이미 취소된 구독입니다.", HttpStatus.BAD_REQUEST),

    // user
    USER_NOT_FOUND("유저가 존재하지 않습니다.", HttpStatus.NOT_FOUND),

    // subscription
    SUBSCRIPSTION_NOT_FOUND("활성화된 구독을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // payment
    PAYMENT_NOT_FOUND("결제 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),

    // comment
    COMMENT_NOT_FOUND("댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_USER_ID("잘못된 요청입니다 (유효하지 않은 유저 ID).", HttpStatus.BAD_REQUEST),
    INVALID_COMMENT_ID("잘못된 요청입니다 (유효하지 않은 댓글 ID).", HttpStatus.BAD_REQUEST),
    INVALID_FRIEND_ID("잘못된 요청입니다 (유효하지 않은 친구 ID).", HttpStatus.BAD_REQUEST);

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
