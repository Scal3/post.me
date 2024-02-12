package com.herman.postme.exception.exceptionimp;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends BaseAppException {
    public UserAlreadyExistsException(String reason) {
        super(HttpStatus.CONFLICT, reason);
    }
}
