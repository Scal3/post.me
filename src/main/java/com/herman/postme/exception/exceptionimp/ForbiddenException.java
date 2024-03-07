package com.herman.postme.exception.exceptionimp;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseAppException {

    public ForbiddenException(String reason) {
        super(HttpStatus.FORBIDDEN, reason);
    }
}
