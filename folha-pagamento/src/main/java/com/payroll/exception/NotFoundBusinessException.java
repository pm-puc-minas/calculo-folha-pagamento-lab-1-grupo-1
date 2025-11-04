package com.payroll.exception;

import com.payroll.exception.base.AbstractBusinessException;

public class NotFoundBusinessException extends AbstractBusinessException {
    public NotFoundBusinessException(String message) {
        super(message);
    }
    public NotFoundBusinessException(String message, Object context) {
        super(message, context);
    }
}