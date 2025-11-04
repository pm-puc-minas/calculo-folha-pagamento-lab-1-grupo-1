package com.payroll.collections;

public interface FilterSpec<T> {
    boolean isValid(T item);
}