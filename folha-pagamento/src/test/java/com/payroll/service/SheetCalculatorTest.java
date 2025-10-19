package com.payroll.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.payroll.model.Employee.GrauInsalubridade;

class SheetCalculatorTest {

    private SheetCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new SheetCalculator();
    }

    @Test
    void testCalcularSalarioHora() {
        BigDecimal salarioHora = calculator.calcularSalarioHora(new BigDecimal("3000.00"), 40);
        salarioHora = salarioHora.setScale(2, RoundingMode.HALF_UP);

        assertEquals(new BigDecimal("17.32"), salarioHora,
                "O salário por hora deve ser calculado corretamente");

        // Teste para entradas nulas ou horas = 0
        assertEquals(BigDecimal.ZERO, calculator.calcularSalarioHora(null, 40));
        assertEquals(BigDecimal.ZERO, calculator.calcularSalarioHora(new BigDecimal("3000.00"), 0));
    }

    @Test
    void testCalcularAdicionalPericulosidade() {
        BigDecimal adicional = calculator.calcularAdicionalPericulosidade(new BigDecimal("3000.00"));
        assertEquals(new BigDecimal("900.00"), adicional);
        assertEquals(BigDecimal.ZERO, calculator.calcularAdicionalPericulosidade(null));
    }

    @Test
    void testCalcularAdicionalInsalubridade() {
        assertEquals(new BigDecimal("141.20"), calculator.calcularAdicionalInsalubridade(new BigDecimal("1412.00"), GrauInsalubridade.BAIXO));
        assertEquals(new BigDecimal("282.40"), calculator.calcularAdicionalInsalubridade(new BigDecimal("1412.00"), GrauInsalubridade.MEDIO));
        assertEquals(new BigDecimal("564.80"), calculator.calcularAdicionalInsalubridade(new BigDecimal("1412.00"), GrauInsalubridade.ALTO));
        assertEquals(BigDecimal.ZERO, calculator.calcularAdicionalInsalubridade(new BigDecimal("1412.00"), GrauInsalubridade.NENHUM));
        assertEquals(BigDecimal.ZERO, calculator.calcularAdicionalInsalubridade(null, GrauInsalubridade.MEDIO));
    }

    @Test
    void testCalcularDescontoValeTransporte() {
        BigDecimal desconto = calculator.calcularDescontoValeTransporte(new BigDecimal("3000.00"), new BigDecimal("150.00"));
        assertEquals(new BigDecimal("150.00"), desconto); // Menor entre 6% do salário e valor entregue
        desconto = calculator.calcularDescontoValeTransporte(new BigDecimal("3000.00"), new BigDecimal("300.00"));
        assertEquals(new BigDecimal("180.00"), desconto); // 6% de 3000 = 180
        assertEquals(BigDecimal.ZERO, calculator.calcularDescontoValeTransporte(null, new BigDecimal("100")));
        assertEquals(BigDecimal.ZERO, calculator.calcularDescontoValeTransporte(new BigDecimal("3000"), null));
    }

    @Test
    void testCalcularValeAlimentacao() {
        BigDecimal vale = calculator.calcularValeAlimentacao(new BigDecimal("25.00"), 22);
        assertEquals(new BigDecimal("550.00"), vale);
        assertEquals(BigDecimal.ZERO, calculator.calcularValeAlimentacao(null, 22));
        assertEquals(BigDecimal.ZERO, calculator.calcularValeAlimentacao(new BigDecimal("25.00"), 0));
    }

    @Test
    void testCalcularINSS() {
        BigDecimal inss = calculator.calcularINSS(new BigDecimal("3000.00"));
        assertTrue(inss.compareTo(BigDecimal.ZERO) > 0);
        assertEquals(BigDecimal.ZERO, calculator.calcularINSS(null));
        assertEquals(BigDecimal.ZERO, calculator.calcularINSS(BigDecimal.ZERO));
    }

    @Test
    void testCalcularFGTS() {
        BigDecimal fgts = calculator.calcularFGTS(new BigDecimal("3000.00"));
        assertEquals(new BigDecimal("240.00"), fgts);
        assertEquals(BigDecimal.ZERO, calculator.calcularFGTS(null));
    }
    
    @Test
    void testCalcularIRRF() {
        BigDecimal inss = new BigDecimal("330.00");
        BigDecimal irrf = calculator.calcularIRRF(new BigDecimal("3000.00"), inss, 0);
        assertTrue(irrf.compareTo(BigDecimal.ZERO) > 0); //Esse assert não testa o comportamento esperado
        irrf = calculator.calcularIRRF(new BigDecimal("3000.00"), inss, 2);
        assertTrue(irrf.compareTo(BigDecimal.ZERO) > 0); //Esse assert aqui não testa o comportamento esperado do método.
        assertEquals(BigDecimal.ZERO, calculator.calcularIRRF(null, inss, 0));
        assertEquals(BigDecimal.ZERO, calculator.calcularIRRF(new BigDecimal("3000.00"), null, 0));
    }

    
}