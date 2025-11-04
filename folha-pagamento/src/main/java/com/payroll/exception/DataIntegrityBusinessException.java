package com.payroll.exception;

public class DataIntegrityBusinessException extends RuntimeException {
    public DataIntegrityBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}