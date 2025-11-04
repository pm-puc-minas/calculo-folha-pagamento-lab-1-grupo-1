package com.payroll.exception;

import java.util.Map;
import com.payroll.exception.base.AbstractBusinessException;

public class InputValidationException extends AbstractBusinessException {
    public InputValidationException(String message) {
        super(message);
    }
    public InputValidationException(String message, Map<String, Object> context) {
        super(message, context);
    }
}