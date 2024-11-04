package com.splanet.splanet.core.exception;

public class InvalidPlanFormatException extends BusinessException {

    public InvalidPlanFormatException(String messageContent) {
        super(ErrorCode.INVALID_PLAN_FORMAT, messageContent);
    }
}
