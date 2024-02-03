package com.herman.postme.exception.exceptionimp;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Getter
public class BaseAppException extends ResponseStatusException {
    private final int code;
    private final String error;
    private final String description;
    private final String path;
    private final LocalDateTime time;

    public BaseAppException(HttpStatus status, String reason, String path) {
        super(status, reason);
        this.description =  reason;
        this.path = path;
        this.code = status.value();
        this.time = LocalDateTime.now();
        this.error = status.getReasonPhrase();
    }
}