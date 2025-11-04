package com.payroll.collections;

public interface GroupBySpec<K, T> {
    K key(T item);
}