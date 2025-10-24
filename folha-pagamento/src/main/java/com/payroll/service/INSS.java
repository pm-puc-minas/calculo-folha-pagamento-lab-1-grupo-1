package com.payroll.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Calcula o desconto de INSS usando tabela progressiva por faixas.
public class INSS implements SheetCalculator.Desconto {

    @Override
    // Calcula INSS progressivo por faixas (2024)
    public BigDecimal calcular(SheetCalculator.DescontoContext ctx) {
        BigDecimal salarioContribuicao = ctx.getSalarioBruto();
        if (salarioContribuicao == null || salarioContribuicao.compareTo(BigDecimal.ZERO) <= 0)
            return BigDecimal.ZERO;

        BigDecimal totalDesconto = BigDecimal.ZERO;
        BigDecimal salarioRestante = salarioContribuicao;

        for (int i = 0; i < PayrollConstants.INSS_LIMITS.length && salarioRestante.compareTo(BigDecimal.ZERO) > 0; i++) {
            BigDecimal limite = PayrollConstants.INSS_LIMITS[i];
            BigDecimal limiteAnterior = i > 0 ? PayrollConstants.INSS_LIMITS[i - 1] : BigDecimal.ZERO;
            BigDecimal valorTributavel = salarioRestante.min(limite.subtract(limiteAnterior));

            if (valorTributavel.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal desconto = valorTributavel.multiply(PayrollConstants.INSS_RATES[i]);
                totalDesconto = totalDesconto.add(desconto);
                salarioRestante = salarioRestante.subtract(valorTributavel);
            }
        }

        return totalDesconto.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    // Prioridade: 1 (antes do IRRF)
    public int prioridade() { return 1; }
}