package com.frameworkium.jira.exceptions;

public class CsvException extends RuntimeException {

    public CsvException(String message) {
        super(message);
    }

    public CsvException(Throwable cause) {
        super(cause);
    }
}
