package com.payroll.service;

import java.math.BigDecimal;

import com.payroll.service.SheetCalculator.DescontoContext;

public interface IDesconto {
    BigDecimal calcular(DescontoContext ctx);

    default int prioridade() {
        return 0;
    }

    default String nome() {
        return getClass().getSimpleName();
    }
}