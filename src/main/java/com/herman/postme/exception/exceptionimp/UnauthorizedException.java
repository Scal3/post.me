package com.herman.postme.exception.exceptionimp;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BaseAppException {
    public UnauthorizedException(String reason) {
        super(HttpStatus.UNAUTHORIZED, reason);
    }

}
