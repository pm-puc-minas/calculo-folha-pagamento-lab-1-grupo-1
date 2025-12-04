package com.payroll.service;

import com.payroll.model.Employee;

import java.math.BigDecimal;

/**
 * Strategy para calcular adicionais/benefícios (periculosidade, insalubridade, vale alimentação etc.).
 */
public interface IAdicional {
    BigDecimal calcular(AdicionalContext ctx);

    default String nome() {
        return getClass().getSimpleName();
    }

    default int prioridade() {
        return 0;
    }

    /**
     * Contexto de cálculo de proventos/adicionais.
     */
    class AdicionalContext {
        private final BigDecimal salarioBase;
        private final BigDecimal salarioMinimo;
        private final Employee.GrauInsalubridade grauInsalubridade;
        private final BigDecimal valeAlimentacaoDiario;
        private final int diasTrabalhados;
        private final boolean perigoso;

        public AdicionalContext(BigDecimal salarioBase,
                                BigDecimal salarioMinimo,
                                Employee.GrauInsalubridade grauInsalubridade,
                                BigDecimal valeAlimentacaoDiario,
                                int diasTrabalhados,
                                boolean perigoso) {
            this.salarioBase = salarioBase;
            this.salarioMinimo = salarioMinimo;
            this.grauInsalubridade = grauInsalubridade;
            this.valeAlimentacaoDiario = valeAlimentacaoDiario;
            this.diasTrabalhados = diasTrabalhados;
            this.perigoso = perigoso;
        }

        public BigDecimal getSalarioBase() { return salarioBase; }
        public BigDecimal getSalarioMinimo() { return salarioMinimo; }
        public Employee.GrauInsalubridade getGrauInsalubridade() { return grauInsalubridade; }
        public BigDecimal getValeAlimentacaoDiario() { return valeAlimentacaoDiario; }
        public int getDiasTrabalhados() { return diasTrabalhados; }
        public boolean isPerigoso() { return perigoso; }
    }
}
