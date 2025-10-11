package com.payroll.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class PayrollTest {

    private Payroll payroll;

    @BeforeEach
    void setUp() {
        payroll = new Payroll("2025-10");
        payroll.setValorAdicionalPericulosidade(BigDecimal.valueOf(500));
        payroll.setValorAdicionalInsalubridade(BigDecimal.valueOf(300));
        payroll.setValorValeAlimentacao(BigDecimal.valueOf(200));
        payroll.setValorDescontoINSS(BigDecimal.valueOf(400));
        payroll.setValorDescontoIRRF(BigDecimal.valueOf(150));
        payroll.setValorDescontoValeTransporte(BigDecimal.valueOf(100));
    }

    // Testa cálculo total de proventos considerando adicionais e proventos extras
    @Test
    void testCalcularTotalProventos() {
        payroll.adicionarProvento(BigDecimal.valueOf(100)); // provento extra
        BigDecimal totalProventos = payroll.calcularTotalProventos();
        assertEquals(BigDecimal.valueOf(1100).setScale(2), totalProventos);
    }

    // Testa cálculo total de descontos considerando descontos extras
    @Test
    void testCalcularTotalDesconto() {
        payroll.adicionarDesconto(BigDecimal.valueOf(50)); // desconto extra
        BigDecimal totalDescontos = payroll.calcularTotalDesconto();
        assertEquals(BigDecimal.valueOf(700).setScale(2), totalDescontos);
    }

    // Testa cálculo do salário líquido
    @Test
    void testCalcularSalarioLiquido() {
        payroll.adicionarProvento(BigDecimal.valueOf(100));
        payroll.adicionarDesconto(BigDecimal.valueOf(50));
        payroll.calcular(); // atualiza totalProventos, totalDescontos e salarioLiquido
        assertEquals(BigDecimal.valueOf(400).setScale(2), payroll.getSalarioLiquido());
    }

    // Testa adicionar proventos
    @Test
    void testAdicionarProvento() {
        payroll.adicionarProvento(BigDecimal.valueOf(50));
        assertTrue(payroll.calcularTotalProventos().compareTo(BigDecimal.valueOf(1050).setScale(2)) == 0);
    }
    
}
    