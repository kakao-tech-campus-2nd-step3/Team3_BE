package com.splanet.splanet.core.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    ACCESS_DENIED("권한이 없습니다.", HttpStatus.FORBIDDEN),
    UNAUTHORIZED("인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED),
    ALREADY_CANCELED("이미 취소된 구독입니다.", HttpStatus.BAD_REQUEST),
    SUBSCRIPSTION_NOT_FOUND("활성화된 구독을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    TEAM_NOT_FOUND("팀을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    TEAM_MEMBER_NOT_FOUND("해당 유저는 팀에 속해 있지 않습니다.", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND("유저가 존재하지 않습니다.", HttpStatus.NOT_FOUND);


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
