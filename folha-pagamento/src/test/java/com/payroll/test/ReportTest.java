package com.payroll.test;

import java.math.BigDecimal;

import com.payroll.model.Payroll;
import com.payroll.model.Report;

public class ReportTest {

    public static void main(String[] args) {
        // Criar um objeto Payroll e preencher com dados
        Payroll payroll = new Payroll();
        payroll.setMesReferencia("11/2025");
        payroll.setValorAdicionalPericulosidade(new BigDecimal("200.00"));
        payroll.setValorAdicionalInsalubridade(new BigDecimal("150.00"));
        payroll.setValorValeAlimentacao(new BigDecimal("300.00"));
        payroll.setTotalProventos(new BigDecimal("650.00"));
        payroll.setValorDescontoINSS(new BigDecimal("100.00"));
        payroll.setValorDescontoIRRF(new BigDecimal("50.00"));
        payroll.setValorDescontoValeTransporte(new BigDecimal("80.00"));
        payroll.setTotalDescontos(new BigDecimal("230.00"));
        payroll.setValorFGTS(new BigDecimal("60.00"));
        payroll.setSalarioLiquido(new BigDecimal("420.00"));

        // Criar o relatório
        Report report = new Report(payroll);

        // Exibir o demonstrativo
        report.exibirDemonstrativo(payroll);
    }
}
