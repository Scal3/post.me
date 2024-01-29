package com.herman.postme.exception.exceptionimp;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseAppException {
    public NotFoundException(String reason, String path) {
        super(HttpStatus.NOT_FOUND, reason, path);
    }
}
