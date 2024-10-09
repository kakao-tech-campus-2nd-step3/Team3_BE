package com.splanet.splanet.core.exception;

public class ResponseConstants {

    public static class ErrorMessage {
        public static final String VALIDATION_ERROR = "유효성 검사에 실패하였습니다.";
        public static final String DUPLICATE_NICKNAME = "닉네임이 중복되었습니다.";
        public static final String INVALID_INPUT = "입력값이 유효하지 않습니다.";
    }

    public static class StatusCode {
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int NOT_FOUND = 404;
        public static final int INTERNAL_SERVER_ERROR = 500;
    }
}
