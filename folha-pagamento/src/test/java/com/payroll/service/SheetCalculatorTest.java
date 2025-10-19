package com.payroll.service;

import com.payroll.model.Employee.GrauInsalubridade;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class SheetCalculatorTest {

    private SheetCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new SheetCalculator();
    }

    @Test
    @DisplayName("Calcula salário por hora corretamente e lida com nulos/zero")
    // Verifica cálculo correto de salário/hora e tratamento de entradas inválidas
    void deveCalcularSalarioPorHora() {
        BigDecimal salarioHora = calculator.calcularSalarioHora(new BigDecimal("3000.00"), 40);
        salarioHora = salarioHora.setScale(2, RoundingMode.HALF_UP);

        assertEquals(new BigDecimal("17.32"), salarioHora,
                "O salário por hora deve ser calculado corretamente");

        // Teste para entradas nulas ou horas = 0
        assertEquals(BigDecimal.ZERO, calculator.calcularSalarioHora(null, 40));
        assertEquals(BigDecimal.ZERO, calculator.calcularSalarioHora(new BigDecimal("3000.00"), 0));
    }

    @Test
    @DisplayName("Calcula adicional de periculosidade e nulos")
    // Verifica cálculo do adicional de periculosidade e entradas nulas
    void deveCalcularAdicionalPericulosidade() {
        BigDecimal adicional = calculator.calcularAdicionalPericulosidade(new BigDecimal("3000.00"));
        assertEquals(new BigDecimal("900.00"), adicional);
        assertEquals(BigDecimal.ZERO, calculator.calcularAdicionalPericulosidade(null));
    }

    @Test
    @DisplayName("Calcula adicional de insalubridade por grau")
    // Verifica cálculo do adicional de insalubridade para cada grau
    void deveCalcularAdicionalInsalubridadePorGrau() {
        assertEquals(new BigDecimal("141.20"), calculator.calcularAdicionalInsalubridade(new BigDecimal("1412.00"), GrauInsalubridade.BAIXO));
        assertEquals(new BigDecimal("282.40"), calculator.calcularAdicionalInsalubridade(new BigDecimal("1412.00"), GrauInsalubridade.MEDIO));
        assertEquals(new BigDecimal("564.80"), calculator.calcularAdicionalInsalubridade(new BigDecimal("1412.00"), GrauInsalubridade.ALTO));
        assertEquals(BigDecimal.ZERO, calculator.calcularAdicionalInsalubridade(new BigDecimal("1412.00"), GrauInsalubridade.NENHUM));
        assertEquals(BigDecimal.ZERO, calculator.calcularAdicionalInsalubridade(null, GrauInsalubridade.MEDIO));
    }

    @Test
    @DisplayName("Calcula menor entre 6% e valor entregue")
    // Verifica desconto do vale transporte como menor entre 6% e entregue
    void deveCalcularDescontoValeTransporte() {
        BigDecimal desconto = calculator.calcularDescontoValeTransporte(new BigDecimal("3000.00"), new BigDecimal("150.00"));
        assertEquals(new BigDecimal("150.00"), desconto); // Menor entre 6% do salário e valor entregue
        desconto = calculator.calcularDescontoValeTransporte(new BigDecimal("3000.00"), new BigDecimal("300.00"));
        assertEquals(new BigDecimal("180.00"), desconto); // 6% de 3000 = 180
        assertEquals(BigDecimal.ZERO, calculator.calcularDescontoValeTransporte(null, new BigDecimal("100")));
        assertEquals(BigDecimal.ZERO, calculator.calcularDescontoValeTransporte(new BigDecimal("3000"), null));
    }

    @Test
    @DisplayName("Calcula vale alimentação")
    // Verifica cálculo do vale alimentação e entradas inválidas
    void deveCalcularValeAlimentacao() {
        BigDecimal vale = calculator.calcularValeAlimentacao(new BigDecimal("25.00"), 22);
        assertEquals(new BigDecimal("550.00"), vale);
        assertEquals(BigDecimal.ZERO, calculator.calcularValeAlimentacao(null, 22));
        assertEquals(BigDecimal.ZERO, calculator.calcularValeAlimentacao(new BigDecimal("25.00"), 0));
    }

    @Test
    @DisplayName("Calcula INSS e lida com nulos")
    // Verifica cálculo de INSS e tratamento de valores nulos/zero
    void deveCalcularINSS() {
        BigDecimal inss = calculator.calcularINSS(new BigDecimal("3000.00"));
        assertTrue(inss.compareTo(BigDecimal.ZERO) > 0);
        assertEquals(BigDecimal.ZERO, calculator.calcularINSS(null));
        assertEquals(BigDecimal.ZERO, calculator.calcularINSS(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Calcula FGTS e lida com nulos")
    // Verifica cálculo do FGTS e tratamento de valores nulos
    void deveCalcularFGTS() {
        BigDecimal fgts = calculator.calcularFGTS(new BigDecimal("3000.00"));
        assertEquals(new BigDecimal("240.00"), fgts);// Retorna o desconto, 8% do salário bruto
        assertEquals(BigDecimal.ZERO, calculator.calcularFGTS(null));
    }
    
    @Test
    @DisplayName("Calcula IRRF com e sem dependentes e nulos")
    // Verifica cálculo do IRRF retornando valores corretos por faixa e entradas nulas
    void deveCalcularIRRF() {
        BigDecimal inss = new BigDecimal("330.00");
        // Base = salário bruto - INSS - dependentes*189.59
    
        // Sem dependentes: base = 3000 - 330 = 2670.00 → faixa 7,5%, dedução 169,44
        BigDecimal irrfSemDep = calculator.calcularIRRF(new BigDecimal("3000.00"), inss, 0);
        assertEquals(new BigDecimal("30.81"), irrfSemDep);
    
        // Com 2 dependentes: base = 3000 - 330 - (2*189,59) = 2290.82 → mesma faixa
        BigDecimal irrfCom2Dep = calculator.calcularIRRF(new BigDecimal("3000.00"), inss, 2);
        assertEquals(new BigDecimal("2.37"), irrfCom2Dep);
    
        // Isento com 3 dependentes: base <= 2259.20
        BigDecimal irrfIsento = calculator.calcularIRRF(new BigDecimal("3000.00"), inss, 3);
        assertEquals(BigDecimal.ZERO, irrfIsento);
    
        // Entradas nulas
        assertEquals(BigDecimal.ZERO, calculator.calcularIRRF(null, inss, 0));
        assertEquals(BigDecimal.ZERO, calculator.calcularIRRF(new BigDecimal("3000.00"), null, 0));
    }

    
}