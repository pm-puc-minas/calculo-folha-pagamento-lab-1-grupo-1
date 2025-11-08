package com.payroll.collections;
/*
 * Contrato de agrupamento para coleções.
 * Define como obter a chave de agrupamento de um item,
 * permitindo operações de groupBy de forma genérica.
 */

public interface GroupBySpec<K, T> {
    K key(T item);
}