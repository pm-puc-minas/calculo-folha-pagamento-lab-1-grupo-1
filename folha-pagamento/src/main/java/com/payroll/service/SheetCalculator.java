package com.payroll.service;

import com.payroll.model.Employee;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calculadora de descontos e cálculos adicionais da folha.
 * Contém o Contexto de Descontos (DescontoContext).
 */
public class SheetCalculator {
    
    // Contexto compartilhado entre os cálculos (Estratégias)
    public static class DescontoContext {
        private final BigDecimal salarioBruto;
        private BigDecimal salarioBaseCalculo; // Base de cálculo que é atualizada a cada desconto
        private final int dependentes;
        private final BigDecimal pensaoAlimenticia;

        /**
         * Inicializa o contexto com os dados de entrada.
         */
        public DescontoContext(BigDecimal salarioBruto, int dependentes, BigDecimal pensaoAlimenticia) {
            this.salarioBruto = salarioBruto;
            // A base de cálculo começa com o salário bruto
            this.salarioBaseCalculo = salarioBruto; 
            this.dependentes = dependentes;
            this.pensaoAlimenticia = pensaoAlimenticia == null ? BigDecimal.ZERO : pensaoAlimenticia;
            
            // Deduz a pensão alimentícia da base de cálculo (regra fiscal)
            this.salarioBaseCalculo = this.salarioBaseCalculo.subtract(this.pensaoAlimenticia);
        }

        /**
         * Retorna o salário bruto do funcionário.
         */
        public BigDecimal getSalarioBruto() { return salarioBruto; }

        /**
         * Retorna a base de cálculo atual (valor restante após descontos aplicados).
         */
        public BigDecimal getSalarioBaseCalculo() { return salarioBaseCalculo; } 
        
        /**
         * Retorna o número de dependentes.
         */
        public int getDependentes() { return dependentes; }
        
        /**
         * Retorna o valor da pensão alimentícia.
         */
        public BigDecimal getPensaoAlimenticia() { return pensaoAlimenticia; }
        
        /**
         * Atualiza a base de cálculo, subtraindo o valor do desconto aplicado.
         */
        public void aplicarDesconto(BigDecimal valorDesconto) {
            this.salarioBaseCalculo = this.salarioBaseCalculo.subtract(valorDesconto);
        }
    }

    /**
     * Calcula o salário/valor de hora.
     */
    public BigDecimal calcularSalarioHora(BigDecimal salario, int horas) {
        if (salario == null || horas <= 0) return BigDecimal.ZERO;
        return salario.divide(BigDecimal.valueOf(horas), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula o adicional de periculosidade (30%).
     */
    public BigDecimal calcularAdicionalPericulosidade(BigDecimal salario) {
        if (salario == null) return BigDecimal.ZERO;
        return salario.multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula o adicional de insalubridade, variando por grau.
     */
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

    /**
     * Calcula o desconto de Vale Transporte baseado em um percentual.
     */
    public BigDecimal calcularDescontoValeTransporte(BigDecimal salario, BigDecimal desconto) {
        if (salario == null || desconto == null) return BigDecimal.ZERO;
        return salario.multiply(desconto).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula o valor do Vale Alimentação (exemplo por dia trabalhado).
     */
    public BigDecimal calcularValeAlimentacao(BigDecimal salario, int dias) {
        if (salario == null || dias <= 0) return BigDecimal.ZERO;
        return salario.divide(new BigDecimal("30"), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(dias))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula o valor do FGTS (8%).
     */
    public BigDecimal calcularFGTS(BigDecimal salario) {
        if (salario == null) return BigDecimal.ZERO;
        return salario.multiply(new BigDecimal("0.08")).setScale(2, RoundingMode.HALF_UP);
    }
}