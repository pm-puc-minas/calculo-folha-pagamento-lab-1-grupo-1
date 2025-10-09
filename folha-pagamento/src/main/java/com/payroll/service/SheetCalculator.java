package com.payroll.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import com.payroll.model.Employee.GrauInsalubridade;

public class SheetCalculator {


    // Tabelas de INSS 2024
    private static final BigDecimal[] INSS_LIMITS = {
            new BigDecimal("1412.00"),
            new BigDecimal("2666.68"),
            new BigDecimal("4000.03"),
            new BigDecimal("7786.02")
    };

    private static final BigDecimal[] INSS_RATES = {
            new BigDecimal("0.075"),
            new BigDecimal("0.09"),
            new BigDecimal("0.12"),
            new BigDecimal("0.14")
    };

    // Tabela de IRRF 2024
    private static final BigDecimal[] IRPF_LIMITS = {
            new BigDecimal("2259.20"),
            new BigDecimal("2826.65"),
            new BigDecimal("3751.05"),
            new BigDecimal("4664.68")
    };

    private static final BigDecimal[] IRPF_RATES = {
            new BigDecimal("0.075"),
            new BigDecimal("0.15"),
            new BigDecimal("0.225"),
            new BigDecimal("0.275")
    };

    private static final BigDecimal[] IRPF_DEDUCTIONS = {
            new BigDecimal("169.44"),
            new BigDecimal("381.44"),
            new BigDecimal("662.77"),
            new BigDecimal("896.00")
    };

    public BigDecimal calcularSalarioHora(BigDecimal salarioBruto, int horasSemanais) {
        if (salarioBruto == null || horasSemanais <= 0) {
            return BigDecimal.ZERO;
        }

        // Cálculo baseado em 4.33 semanas por mês (52 semanas / 12 meses)
        BigDecimal horasMensais = new BigDecimal(horasSemanais).multiply(new BigDecimal("4.33"));
        return salarioBruto.divide(horasMensais, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularAdicionalPericulosidade(BigDecimal salarioBase) {
        if (salarioBase == null) {
            return BigDecimal.ZERO;
        }

        // Adicional de periculosidade é 30% do salário base
        return salarioBase.multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularAdicionalInsalubridade(BigDecimal salarioMinimo, GrauInsalubridade grau) {
        if (salarioMinimo == null || grau == null || grau == GrauInsalubridade.NENHUM) {
            return BigDecimal.ZERO;
        }

        BigDecimal percentual = switch (grau) {
            case BAIXO -> new BigDecimal("0.10");   // 10%
            case MEDIO -> new BigDecimal("0.20");   // 20%
            case ALTO -> new BigDecimal("0.40");    // 40%
            default -> BigDecimal.ZERO;
        };

        return salarioMinimo.multiply(percentual).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularDescontoValeTransporte(BigDecimal salarioBruto, BigDecimal valorEntregue) {
        if (salarioBruto == null || valorEntregue == null) {
            return BigDecimal.ZERO;
        }

        // Desconto máximo de 6% do salário bruto
        BigDecimal descontoMaximo = salarioBruto.multiply(new BigDecimal("0.06"));

        // O desconto é o menor entre o valor entregue e 6% do salário
        return valorEntregue.min(descontoMaximo).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularValeAlimentacao(BigDecimal valorDiario, int diasTrabalhados) {
        if (valorDiario == null || diasTrabalhados <= 0) {
            return BigDecimal.ZERO;
        }

        return valorDiario.multiply(new BigDecimal(diasTrabalhados)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularINSS(BigDecimal salarioContribuicao) {
        if (salarioContribuicao == null || salarioContribuicao.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalDesconto = BigDecimal.ZERO;
        BigDecimal salarioRestante = salarioContribuicao;

        for (int i = 0; i < INSS_LIMITS.length && salarioRestante.compareTo(BigDecimal.ZERO) > 0; i++) {
            BigDecimal limite = INSS_LIMITS[i];
            BigDecimal limiteAnterior = i > 0 ? INSS_LIMITS[i - 1] : BigDecimal.ZERO;
            BigDecimal valorTributavel = salarioRestante.min(limite.subtract(limiteAnterior));

            if (valorTributavel.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal desconto = valorTributavel.multiply(INSS_RATES[i]);
                totalDesconto = totalDesconto.add(desconto);
                salarioRestante = salarioRestante.subtract(valorTributavel);
            }
        }

        return totalDesconto.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularFGTS(BigDecimal baseCalculoFGTS) {
        if (baseCalculoFGTS == null || baseCalculoFGTS.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // FGTS é sempre 8% do salário bruto
        return baseCalculoFGTS.multiply(new BigDecimal("0.08")).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularIRRF(BigDecimal salarioBruto, BigDecimal descontoINSS, int numDependentes) {
        if (salarioBruto == null || descontoINSS == null) {
            return BigDecimal.ZERO;
        }

        // Base de cálculo: salário bruto - INSS - (dependentes * 189.59)
        BigDecimal deducaoDependentes = new BigDecimal("189.59").multiply(new BigDecimal(numDependentes));
        BigDecimal baseCalculo = salarioBruto.subtract(descontoINSS).subtract(deducaoDependentes);

        // Verificar se está isento
        if (baseCalculo.compareTo(IRPF_LIMITS[0]) <= 0) {
            return BigDecimal.ZERO;
        }

        // Calcular IRRF por faixa
        for (int i = IRPF_LIMITS.length - 1; i >= 0; i--) {
            if (baseCalculo.compareTo(IRPF_LIMITS[i]) > 0) {
                BigDecimal imposto = baseCalculo.multiply(IRPF_RATES[i]).subtract(IRPF_DEDUCTIONS[i]);
                return imposto.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
            }
        }

        return BigDecimal.ZERO;
    }
}
