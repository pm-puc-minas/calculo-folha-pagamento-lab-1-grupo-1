package com.payroll.service;

import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SheetCalculatorTest {

    @Test
    @DisplayName("Delegação de INSS funciona e trata nulos")
    void deveDelegarCalculoINSS() {
        BigDecimal inss = SheetCalculator.calcularINSS(new BigDecimal("3000.00"));
        assertTrue(inss.compareTo(BigDecimal.ZERO) > 0);
        assertEquals(BigDecimal.ZERO, SheetCalculator.calcularINSS(null));
        assertEquals(BigDecimal.ZERO, SheetCalculator.calcularINSS(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("IRRF simplificado: base*alíquota, com e sem dependentes")
    void deveCalcularIRRFSimplificado() {
        BigDecimal salario = new BigDecimal("3000.00");
        BigDecimal inss = SheetCalculator.calcularINSS(salario);

        // Sem dependentes (faixa 7,5%)
        BigDecimal irrfSemDep = SheetCalculator.calcularIRRF(salario, inss, 0);
        assertEquals(new BigDecimal("205.59"), irrfSemDep);

        // Com 2 dependentes (reduz base, ainda 7,5%)
        BigDecimal irrfCom2 = SheetCalculator.calcularIRRF(salario, inss, 2);
        assertEquals(new BigDecimal("177.15"), irrfCom2);

        // Com 3 dependentes (base isenta)
        BigDecimal irrfIsento = SheetCalculator.calcularIRRF(salario, inss, 3);
        assertEquals(BigDecimal.ZERO, irrfIsento);

        // Entradas nulas
        assertEquals(BigDecimal.ZERO, SheetCalculator.calcularIRRF(null, inss, 0));
        assertEquals(BigDecimal.ZERO, SheetCalculator.calcularIRRF(salario, null, 0));
    }
}