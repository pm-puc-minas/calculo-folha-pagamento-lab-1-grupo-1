package com.payroll.exception;

import com.payroll.exception.base.AbstractBusinessException;

public class InternalServerBusinessException extends AbstractBusinessException {
    public InternalServerBusinessException(String message) {
        super(message);
    }
    public InternalServerBusinessException(String message, Throwable cause) {
        super(message, null, cause);
    }
}