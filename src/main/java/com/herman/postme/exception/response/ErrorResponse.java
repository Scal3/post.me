package com.herman.postme.exception.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ErrorResponse  {
    private final int code;
    private final String error;
    private final String description;
    private final String path;
    private final LocalDateTime time;
}