package com.payroll.service;

import com.payroll.model.Employee;
import com.payroll.model.Employee.GrauInsalubridade;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SheetCalculator {

    // Tabelas INSS e IRRF continuam iguais...

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

    // Agora o método recebe Employee ao invés de valores soltos

    public BigDecimal calcularSalarioHora(Employee employee) {
        if (employee == null || employee.getSalarioBruto() == null || employee.getHorasSemanais() <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal horasMensais = new BigDecimal(employee.getHorasSemanais()).multiply(new BigDecimal("4.33"));
        return employee.getSalarioBruto().divide(horasMensais, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularAdicionalPericulosidade(Employee employee) {
        if (employee == null || employee.getSalarioBruto() == null) {
            return BigDecimal.ZERO;
        }

        return employee.getSalarioBruto().multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularAdicionalInsalubridade(Employee employee) {
        if (employee == null || employee.getSalarioMinimo() == null || employee.getGrauInsalubridade() == null || employee.getGrauInsalubridade() == GrauInsalubridade.NENHUM) {
            return BigDecimal.ZERO;
        }

        BigDecimal percentual = switch (employee.getGrauInsalubridade()) {
            case BAIXO -> new BigDecimal("0.10");
            case MEDIO -> new BigDecimal("0.20");
            case ALTO -> new BigDecimal("0.40");
            default -> BigDecimal.ZERO;
        };

        return employee.getSalarioMinimo().multiply(percentual).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularDescontoValeTransporte(Employee employee, BigDecimal valorEntregue) {
        if (employee == null || employee.getSalarioBruto() == null || valorEntregue == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal descontoMaximo = employee.getSalarioBruto().multiply(new BigDecimal("0.06"));
        return valorEntregue.min(descontoMaximo).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularValeAlimentacao(BigDecimal valorDiario, int diasTrabalhados) {
        if (valorDiario == null || diasTrabalhados <= 0) {
            return BigDecimal.ZERO;
        }

        return valorDiario.multiply(new BigDecimal(diasTrabalhados)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularINSS(Employee employee) {
        if (employee == null || employee.getSalarioBruto() == null || employee.getSalarioBruto().compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalDesconto = BigDecimal.ZERO;
        BigDecimal salarioRestante = employee.getSalarioBruto();

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

    public BigDecimal calcularFGTS(Employee employee) {
        if (employee == null || employee.getSalarioBruto() == null || employee.getSalarioBruto().compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return employee.getSalarioBruto().multiply(new BigDecimal("0.08")).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularIRRF(Employee employee, BigDecimal descontoINSS) {
        if (employee == null || employee.getSalarioBruto() == null || descontoINSS == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal deducaoDependentes = new BigDecimal("189.59").multiply(new BigDecimal(employee.getNumDependentes()));
        BigDecimal baseCalculo = employee.getSalarioBruto().subtract(descontoINSS).subtract(deducaoDependentes);

        if (baseCalculo.compareTo(IRPF_LIMITS[0]) <= 0) {
            return BigDecimal.ZERO;
        }

        for (int i = IRPF_LIMITS.length - 1; i >= 0; i--) {
            if (baseCalculo.compareTo(IRPF_LIMITS[i]) > 0) {
                BigDecimal imposto = baseCalculo.multiply(IRPF_RATES[i]).subtract(IRPF_DEDUCTIONS[i]);
                return imposto.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
            }
        }

        return BigDecimal.ZERO;
    }
}
