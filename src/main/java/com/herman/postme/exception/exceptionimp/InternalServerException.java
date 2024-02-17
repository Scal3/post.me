package com.herman.postme.exception.exceptionimp;

import org.springframework.http.HttpStatus;

public class InternalServerException extends BaseAppException {
    public InternalServerException(String reason) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, reason);
    }

}
