package com.splanet.splanet.core.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // 공통
    ACCESS_DENIED("권한이 없습니다.", HttpStatus.FORBIDDEN),
    UNAUTHORIZED("인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_INPUT_VALUE("유효하지 않은 입력 값입니다.", HttpStatus.BAD_REQUEST),

    // User
    USER_NOT_FOUND("유저가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_NICKNAME("닉네임이 중복되었습니다.", HttpStatus.BAD_REQUEST),

    // Plan
    PLAN_NOT_FOUND("플랜이 존재하지 않습니다.", HttpStatus.NOT_FOUND),

    // Subscription
    ALREADY_CANCELED("이미 취소된 구독입니다.", HttpStatus.BAD_REQUEST),
    SUBSCRIPTION_NOT_FOUND("활성화된 구독을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // payment
    PAYMENT_NOT_FOUND("결제 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),

    // comment
    COMMENT_NOT_FOUND("댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_USER_ID("잘못된 요청입니다 (유효하지 않은 유저 ID).", HttpStatus.BAD_REQUEST),
    INVALID_COMMENT_ID("잘못된 요청입니다 (유효하지 않은 댓글 ID).", HttpStatus.BAD_REQUEST),
    INVALID_FRIEND_ID("잘못된 요청입니다 (유효하지 않은 친구 ID).", HttpStatus.BAD_REQUEST),

    // team
    TEAM_NOT_FOUND("팀을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    TEAM_MEMBER_NOT_FOUND("해당 유저는 팀에 속해 있지 않습니다.", HttpStatus.NOT_FOUND),
    INVITATION_NOT_FOUND("초대를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVITATION_ALREADY_PROCESSED("초대가 이미 처리되었습니다.", HttpStatus.BAD_REQUEST),
    USER_ALREADY_IN_TEAM("해당 유저는 이미 팀에 속해 있습니다.", HttpStatus.BAD_REQUEST),

    // redis
    REDIS_SCAN_FAILED("Redis 키 스캔 중 오류가 발생했습니다.", HttpStatus.SERVICE_UNAVAILABLE);

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
