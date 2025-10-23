package com.payroll.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.payroll.model.Employee.GrauInsalubridade;


/**
 * - IRRF = Base de cálculo × Alíquota
 * - Dedução = valor esperado (resultado final)
 */
public class SheetCalculator {

    // --- Salário-hora ---
    public static BigDecimal calcularSalarioHora(BigDecimal salarioBruto, int horasSemanais) {
        if (salarioBruto == null || horasSemanais <= 0) return BigDecimal.ZERO;
        BigDecimal horasMensais = new BigDecimal(horasSemanais).multiply(new BigDecimal("4.33"));
        return salarioBruto.divide(horasMensais, 2, RoundingMode.HALF_UP);
    }

    // --- Adicional de periculosidade (30% do salário base) ---
    public static BigDecimal calcularAdicionalPericulosidade(BigDecimal salarioBase) {
        if (salarioBase == null) return BigDecimal.ZERO;
        return salarioBase.multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);
    }

    // --- Adicional de insalubridade ---
    public static BigDecimal calcularAdicionalInsalubridade(BigDecimal salarioMinimo, GrauInsalubridade grau) {
        if (salarioMinimo == null || grau == null || grau == GrauInsalubridade.NENHUM) return BigDecimal.ZERO;
        BigDecimal percentual = switch (grau) {
            case BAIXO -> new BigDecimal("0.10");
            case MEDIO -> new BigDecimal("0.20");
            case ALTO -> new BigDecimal("0.40");
            default -> BigDecimal.ZERO;
        };
        return salarioMinimo.multiply(percentual).setScale(2, RoundingMode.HALF_UP);
    }

    // --- Desconto de vale transporte ---
    public static BigDecimal calcularDescontoValeTransporte(BigDecimal salarioBruto, BigDecimal valorEntregue) {
        if (salarioBruto == null || valorEntregue == null) return BigDecimal.ZERO;
        BigDecimal descontoMaximo = salarioBruto.multiply(PayrollConstants.TRANSPORTE_RATE);
        return valorEntregue.min(descontoMaximo).setScale(2, RoundingMode.HALF_UP);
    }

    // --- Vale alimentação ---
    public static BigDecimal calcularValeAlimentacao(BigDecimal valorDiario, int diasTrabalhados) {
        if (valorDiario == null || diasTrabalhados <= 0) return BigDecimal.ZERO;
        return valorDiario.multiply(new BigDecimal(diasTrabalhados)).setScale(2, RoundingMode.HALF_UP);
    }

    // --- INSS progressivo ---
    public static BigDecimal calcularINSS(BigDecimal salarioContribuicao) {
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

    // --- FGTS ---
    public static BigDecimal calcularFGTS(BigDecimal baseCalculoFGTS) {
        if (baseCalculoFGTS == null || baseCalculoFGTS.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        return baseCalculoFGTS.multiply(PayrollConstants.FGTS_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    // --- IRRF conforme PDF do professor ---
    public static BigDecimal calcularIRRF(BigDecimal salarioBruto, BigDecimal descontoINSS, int numDependentes) {
        if (salarioBruto == null || descontoINSS == null) return BigDecimal.ZERO;

        // Base = salário bruto - INSS - (dependentes × 189,59)
        BigDecimal deducaoDependentes = PayrollConstants.DEDUCAO_DEPENDENTE.multiply(new BigDecimal(numDependentes));
        BigDecimal baseCalculo = salarioBruto.subtract(descontoINSS).subtract(deducaoDependentes);

        if (baseCalculo.compareTo(PayrollConstants.IRPF_LIMITS[0]) <= 0) return BigDecimal.ZERO;

        // Aplica a alíquota conforme a faixa
        for (int i = PayrollConstants.IRPF_LIMITS.length - 1; i >= 0; i--) {
            if (baseCalculo.compareTo(PayrollConstants.IRPF_LIMITS[i]) > 0) {
                BigDecimal aliquota = PayrollConstants.IRPF_RATES[i + 1]; // próxima faixa
                BigDecimal irrf = baseCalculo.multiply(aliquota);
                return irrf.setScale(2, RoundingMode.HALF_UP);
            }
        }

        return BigDecimal.ZERO;
    }
}
