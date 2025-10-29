package com.payroll.service;

import com.payroll.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IRPFTest {

    @Test
    @DisplayName("calcularIRPF_teorico_semParcelaDeduzir_variandoDependentes")
    // Calcula IRPF (teórico, sem parcela a deduzir) com salário do Employee e dependentes
    void calcularIRPF_teorico_semParcelaDeduzir_variandoDependentes() {
        Employee e = new Employee();
        e.setSalary(new BigDecimal("3000.00"));

        // Calcula INSS para compor a base do IRPF
        SheetCalculator.DescontoContext ctxInss = new SheetCalculator.DescontoContext(
                e.getSalary(), BigDecimal.ZERO, 0, BigDecimal.ZERO
        );
        BigDecimal inss = new INSS().calcular(ctxInss);

        // Dependentes = 0
        SheetCalculator.DescontoContext ctx0 = new SheetCalculator.DescontoContext(
                e.getSalary(), inss, 0, BigDecimal.ZERO
        );
        assertEquals(new BigDecimal("205.59"), new IRPF().calcular(ctx0));

        // Dependentes = 2
        SheetCalculator.DescontoContext ctx2 = new SheetCalculator.DescontoContext(
                e.getSalary(), inss, 2, BigDecimal.ZERO
        );
        assertEquals(new BigDecimal("177.15"), new IRPF().calcular(ctx2));

        // Dependentes = 3 (isento)
        SheetCalculator.DescontoContext ctx3 = new SheetCalculator.DescontoContext(
                e.getSalary(), inss, 3, BigDecimal.ZERO
        );
        // Usa compareTo para ignorar diferença de escala (0 vs 0.00)
        org.junit.jupiter.api.Assertions.assertEquals(0, new IRPF().calcular(ctx3).compareTo(BigDecimal.ZERO));
    }
}
