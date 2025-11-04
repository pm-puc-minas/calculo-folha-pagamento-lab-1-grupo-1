package com.payroll.exception.base;

public abstract class AbstractBusinessException extends RuntimeException implements BusinessException {
    private final Object context;

    public AbstractBusinessException(String message) {
        super(message);
        this.context = null;
    }

    public AbstractBusinessException(String message, Throwable cause) {
        super(message, cause);
        this.context = null;
    }

    public AbstractBusinessException(String message, Object context) {
        super(message);
        this.context = context;
    }

    public AbstractBusinessException(String message, Object context, Throwable cause) {
        super(message, cause);
        this.context = context;
    }

    @Override
    public Object getContext() {
        return context;
    }
}