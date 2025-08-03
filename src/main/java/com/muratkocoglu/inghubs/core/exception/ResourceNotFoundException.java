package com.muratkocoglu.inghubs.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends BusinessException {

    private static final long serialVersionUID = 1399979780031953061L;

	public ResourceNotFoundException(String errorCode, Object... messageArguments) {
        super(errorCode, messageArguments);
    }
}
