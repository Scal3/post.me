package com.herman.postme.exception.exceptionimp;

import org.springframework.http.HttpStatus;

public class ConflictException extends BaseAppException {
    public ConflictException(String reason) {
        super(HttpStatus.CONFLICT, reason);
    }
}
