package com.payroll.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ReportTest {

    private Payroll payroll;
    private Report report;

    @BeforeEach
    void setUp() {
        payroll = new Payroll("09/2025");
        payroll.setValorAdicionalPericulosidade(BigDecimal.valueOf(500));
        payroll.setValorAdicionalInsalubridade(BigDecimal.valueOf(300));
        payroll.setValorValeAlimentacao(BigDecimal.valueOf(200));
        payroll.setValorDescontoINSS(BigDecimal.valueOf(400));
        payroll.setValorDescontoIRRF(BigDecimal.valueOf(100));
        payroll.setValorDescontoValeTransporte(BigDecimal.valueOf(50));
        payroll.setValorFGTS(BigDecimal.valueOf(450));
        payroll.calcular(); // calcula total proventos, descontos e salário líquido

        report = new Report(payroll);
    }

    // Testa a exibição do demonstrativo de pagamento
    @Test
    @DisplayName("Exibe demonstrativo de pagamento corretamente")
    // Verifica exibição do demonstrativo e valores calculados
    void deveExibirDemonstrativoDePagamento() {
        // Não precisamos validar saída do console em unit test simples, apenas executamos para verificar execução sem erros
        report.exibirDemonstrativo(payroll);

        // Validando valores calculados
        assertEquals(BigDecimal.valueOf(1000).setScale(2), payroll.getTotalProventos());
        assertEquals(BigDecimal.valueOf(550).setScale(2), payroll.getTotalDescontos());
        assertEquals(BigDecimal.valueOf(450).setScale(2), payroll.getSalarioLiquido());
    }
}