package com.br.codegroup.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RestErrorMessage {

    private final HttpStatus status;
    private final String exception;
    private final String message;
    private final String details;

    public RestErrorMessage(HttpStatus status, String exception, String message) {
        this.status = status;
        this.exception = exception;
        this.message = message;
        this.details = null;
    }
}