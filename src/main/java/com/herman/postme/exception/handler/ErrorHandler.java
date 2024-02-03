package com.herman.postme.exception.handler;

import com.herman.postme.exception.exceptionimp.NotFoundException;
import com.herman.postme.exception.response.ErrorResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestControllerAdvice
public class ErrorHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponse handleError(NotFoundException e) {
        return new ErrorResponse(
                e.getCode(),
                e.getError(),
                e.getDescription(),
                e.getPath(),
                e.getTime()
        );
    }
}
