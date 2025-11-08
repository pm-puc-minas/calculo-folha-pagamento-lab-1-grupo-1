package com.payroll.collections;
/*
 * Contrato de filtragem para elementos de uma coleção.
 * Define a regra booleana que indica se um item é válido
 * em operações de seleção e processamento de listas.
 */

public interface FilterSpec<T> {
    boolean isValid(T item);
}