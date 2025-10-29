package com.payroll.service;

import com.payroll.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class INSSTest {

    @Test
    @DisplayName("calcularINSS_deveSerProgressivo_comSalarioDoEmployee")
    // Calcula INSS progressivo nas faixas de 2024 usando o salário do Employee
    void calcularINSS_deveSerProgressivo_comSalarioDoEmployee() {
        Employee e = new Employee();
        e.setSalary(new BigDecimal("3000.00"));

        SheetCalculator.DescontoContext ctx = new SheetCalculator.DescontoContext(
                e.getSalary(), BigDecimal.ZERO, 0, BigDecimal.ZERO
        );

        BigDecimal desconto = new INSS().calcular(ctx);
        assertEquals(new BigDecimal("258.82"), desconto);
    }
}

