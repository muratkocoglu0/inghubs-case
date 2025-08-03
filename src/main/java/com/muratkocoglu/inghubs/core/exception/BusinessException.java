package com.muratkocoglu.inghubs.core.exception;

import java.util.Optional;

public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 4389179839792924554L;

	private final String errorCode;

    private final Object[] messageArguments;

    public BusinessException() {
        this.errorCode = null;
        this.messageArguments = new Object[0];
    }

    public BusinessException(Exception e) {
        super(e.getMessage(), e);
        this.errorCode = null;
        this.messageArguments = new Object[0];
    }

    public BusinessException(String errorCode, Object... messageArguments) {
    	super(errorCode);
    	this.errorCode = errorCode;
        this.messageArguments = messageArguments != null ? messageArguments : new Object[0];
    }

    public Optional<String> getErrorCode() {
        return Optional.ofNullable(errorCode);
    }

    public Object[] getMessageArguments() {
        return this.messageArguments;
    }
}

