package com.payroll.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

// IRPF (teórico): imposto = base * aliquota (sem parcela a deduzir)
public class IRPF implements IDesconto {

    @Override
    // Calcula IRPF teórico (sem parcela a deduzir)
    public BigDecimal calcular(SheetCalculator.DescontoContext ctx) {
        BigDecimal salarioBruto = ctx.getSalarioBruto();
        BigDecimal descontoINSS = ctx.getDescontoINSS() == null ? BigDecimal.ZERO : ctx.getDescontoINSS();
        int dependentes = ctx.getDependentes();
        BigDecimal pensao = ctx.getPensaoAlimenticia() == null ? BigDecimal.ZERO : ctx.getPensaoAlimenticia();

        if (salarioBruto == null) return BigDecimal.ZERO;

        BigDecimal deducaoDependentes = PayrollConstants.DEDUCAO_DEPENDENTE.multiply(new BigDecimal(dependentes));
        BigDecimal base = salarioBruto.subtract(descontoINSS).subtract(deducaoDependentes).subtract(pensao);

        if (base.compareTo(PayrollConstants.IRPF_ISENTO) <= 0) return BigDecimal.ZERO;

        int faixaIndex = faixa(base);
        BigDecimal aliquota = PayrollConstants.IRPF_RATES[faixaIndex];

        BigDecimal imposto = base.multiply(aliquota);
        if (imposto.compareTo(BigDecimal.ZERO) < 0) return BigDecimal.ZERO;
        return imposto.setScale(2, RoundingMode.HALF_UP);
    }

    // Retorna o indice da faixa do IRPF
    private int faixa(BigDecimal base) {
        if (base.compareTo(PayrollConstants.IRPF_LIMITS[0]) <= 0) return 0;
        if (base.compareTo(PayrollConstants.IRPF_LIMITS[1]) <= 0) return 1;
        if (base.compareTo(PayrollConstants.IRPF_LIMITS[2]) <= 0) return 2;
        if (base.compareTo(PayrollConstants.IRPF_LIMITS[3]) <= 0) return 3;
        return 4;
    }

    @Override
    // Prioridade de execucao: 3 (simulacoes)
    public int prioridade() { return 3; }
}