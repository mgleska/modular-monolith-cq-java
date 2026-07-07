package com.mgleska.mmcqjava2.shared.exception;

public class AppEntityVersionException extends AppValidationException {

    public AppEntityVersionException() {
        super("version", "Entity version in database is different than version received in API call.");
    }
}
