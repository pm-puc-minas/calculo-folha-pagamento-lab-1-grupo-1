package com.payroll.exception.base;

public interface BusinessException {
    String getMessage();
    Object getContext();
}