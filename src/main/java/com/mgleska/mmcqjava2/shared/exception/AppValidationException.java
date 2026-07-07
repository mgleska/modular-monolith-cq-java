package com.mgleska.mmcqjava2.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
public class AppValidationException extends IllegalArgumentException {
    private final String field;

    public AppValidationException(String field, String message) {
        this.field = field;
        super(message);
    }

    public String getField() {
        return field;
    }
}
