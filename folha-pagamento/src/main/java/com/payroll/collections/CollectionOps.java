package com.payroll.collections;
/*
 * Utilitários para operações sobre coleções (listas e mapas).
 * Fornece funções de filtro, agrupamento e soma com tratamento
 * seguro de nulos e erros, visando robustez em cálculos e agregações.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CollectionOps {
    private static final Logger logger = LoggerFactory.getLogger(CollectionOps.class);

    private CollectionOps() {}

    public static <T> List<T> filter(List<T> source, FilterSpec<T> spec) {
        if (source == null || spec == null) return Collections.emptyList();
        try {
            return source.stream()
                    .filter(Objects::nonNull)
                    .filter(spec::isValid)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Filter failed: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public static <K, T> Map<K, List<T>> groupBy(List<T> source, GroupBySpec<K, T> spec) {
        if (source == null || spec == null) return Collections.emptyMap();
        try {
            return source.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(spec::key));
        } catch (Exception e) {
            logger.error("GroupBy failed: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    public static <T> BigDecimal sum(List<T> source, Function<T, BigDecimal> mapper) {
        if (source == null || mapper == null) return BigDecimal.ZERO;
        try {
            return source.stream()
                    .filter(Objects::nonNull)
                    .map(item -> safeBigDecimal(mapper.apply(item)))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception e) {
            logger.error("Sum failed: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    private static BigDecimal safeBigDecimal(BigDecimal value) {
        if (value == null) return BigDecimal.ZERO;
        if (value.compareTo(BigDecimal.ZERO) < 0) return BigDecimal.ZERO;
        return value;
    }
}