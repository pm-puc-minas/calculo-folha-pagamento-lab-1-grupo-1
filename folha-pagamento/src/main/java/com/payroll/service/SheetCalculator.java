package com.payroll.service;

import java.math.BigDecimal;

// Calculadora de descontos da folha (somente INSS e IRRF via polimorfismo)
public class SheetCalculator {

    // Estratégia de descontos via polimorfismo
    public static interface Desconto {
        BigDecimal calcular(DescontoContext ctx);
        default int prioridade() { return 0; }
        default String nome() { return getClass().getSimpleName(); }
    }

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

    // INSS progressivo: delega para a classe INSS
    public static BigDecimal calcularINSS(BigDecimal salarioContribuicao) {
        if (salarioContribuicao == null || salarioContribuicao.compareTo(BigDecimal.ZERO) <= 0)
            return BigDecimal.ZERO;
        DescontoContext ctx = new DescontoContext(salarioContribuicao, BigDecimal.ZERO, 0, BigDecimal.ZERO);
        return new INSS().calcular(ctx);
    }

    // IRRF: base = bruto − INSS − dependentes×189,59 − pensão; imposto = base × alíquota
    public static BigDecimal calcularIRRF(BigDecimal salarioBruto, BigDecimal descontoINSS, int numDependentes) {
        if (salarioBruto == null || descontoINSS == null) return BigDecimal.ZERO;
        DescontoContext ctx = new DescontoContext(salarioBruto, descontoINSS, numDependentes, BigDecimal.ZERO);
        return new IRRF().calcular(ctx);
    }
}