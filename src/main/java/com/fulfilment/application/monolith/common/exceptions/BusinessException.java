package com.fulfilment.application.monolith.common.exceptions;

public abstract class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
