package com.payroll.service;

import com.payroll.model.Employee;
import java.math.BigDecimal;
import java.math.RoundingMode;

// Calculadora de descontos da folha (INSS, IRRF e adicionais)
public class SheetCalculator {
    // Contexto compartilhado entre os cálculos
    public static class DescontoContext {
        private final BigDecimal salarioBruto;
        private final BigDecimal descontoINSS;
        private final int dependentes;
        private final BigDecimal pensaoAlimenticia;

        public DescontoContext(BigDecimal salarioBruto, BigDecimal descontoINSS, int dependentes, BigDecimal pensaoAlimenticia) {
            this.salarioBruto = salarioBruto;
            this.descontoINSS = descontoINSS;
            this.dependentes = dependentes;
            this.pensaoAlimenticia = pensaoAlimenticia == null ? BigDecimal.ZERO : pensaoAlimenticia;
        }

        public BigDecimal getSalarioBruto() { return salarioBruto; }
        public BigDecimal getDescontoINSS() { return descontoINSS; }
        public int getDependentes() { return dependentes; }
        public BigDecimal getPensaoAlimenticia() { return pensaoAlimenticia; }
    }

    // INSS progressivo
    public static BigDecimal calcularINSS(BigDecimal salarioContribuicao) {
        if (salarioContribuicao == null || salarioContribuicao.compareTo(BigDecimal.ZERO) <= 0)
            return BigDecimal.ZERO;
        DescontoContext ctx = new DescontoContext(salarioContribuicao, BigDecimal.ZERO, 0, BigDecimal.ZERO);
        return new INSS().calcular(ctx);
    }

    // IRRF
    public static BigDecimal calcularIRRF(BigDecimal salarioBruto, BigDecimal descontoINSS, int numDependentes) {
        if (salarioBruto == null || descontoINSS == null) return BigDecimal.ZERO;
        DescontoContext ctx = new DescontoContext(salarioBruto, descontoINSS, numDependentes, BigDecimal.ZERO);
        return new IRRF().calcular(ctx);
    }

    // ====================== NOVOS MÉTODOS PARA TESTES ======================

    public BigDecimal calcularSalarioHora(BigDecimal salario, int horas) {
        if (salario == null || horas <= 0) return BigDecimal.ZERO;
        return salario.divide(BigDecimal.valueOf(horas), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularAdicionalPericulosidade(BigDecimal salario) {
        if (salario == null) return BigDecimal.ZERO;
        // Adicional de 30%
        return salario.multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularAdicionalInsalubridade(BigDecimal salario, Employee.GrauInsalubridade grau) {
        if (salario == null || grau == null) return BigDecimal.ZERO;
        BigDecimal percentual;
        switch (grau) {
            case BAIXO: percentual = new BigDecimal("0.10"); break;
            case MEDIO: percentual = new BigDecimal("0.20"); break;
            case ALTO: percentual = new BigDecimal("0.40"); break;
            default: percentual = BigDecimal.ZERO;
        }
        return salario.multiply(percentual).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularDescontoValeTransporte(BigDecimal salario, BigDecimal desconto) {
        if (salario == null || desconto == null) return BigDecimal.ZERO;
        return salario.multiply(desconto).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularValeAlimentacao(BigDecimal salario, int dias) {
        if (salario == null || dias <= 0) return BigDecimal.ZERO;
        // Exemplo: vale = salário / 30 * dias
        return salario.divide(new BigDecimal("30"), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(dias))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularFGTS(BigDecimal salario) {
        if (salario == null) return BigDecimal.ZERO;
        // FGTS = 8% do salário
        return salario.multiply(new BigDecimal("0.08")).setScale(2, RoundingMode.HALF_UP);
    }
}
