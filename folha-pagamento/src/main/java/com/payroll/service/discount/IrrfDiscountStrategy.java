package com.payroll.service.discount;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

import com.payroll.service.PayrollConstants;

@Component
public class IrrfDiscountStrategy implements DiscountStrategy {

    @Override
    public DiscountType getType() {
        return DiscountType.IRRF;
    }

    @Override
    public BigDecimal calculate(DiscountCalculationContext context) {
        BigDecimal salarioBruto = context.getGrossSalary();
        BigDecimal descontoINSS = context.getInssDiscount();
        int numDependentes = context.getDependents();
        BigDecimal pensaoAlimenticia = context.getPensionAlimony();

        if (salarioBruto == null || descontoINSS == null) return BigDecimal.ZERO;

        BigDecimal deducaoDependentes = PayrollConstants.DEDUCAO_DEPENDENTE.multiply(new BigDecimal(numDependentes));
        BigDecimal pensao = pensaoAlimenticia != null ? pensaoAlimenticia : BigDecimal.ZERO;
        BigDecimal baseCalculo = salarioBruto.subtract(descontoINSS).subtract(deducaoDependentes).subtract(pensao);

        if (baseCalculo.compareTo(PayrollConstants.IRPF_ISENTO) <= 0) return BigDecimal.ZERO;

        BigDecimal totalIRRF = BigDecimal.ZERO;
        BigDecimal baseRestante = baseCalculo;

        for (int i = 0; i < PayrollConstants.IRPF_LIMITS.length && baseRestante.compareTo(BigDecimal.ZERO) > 0; i++) {
            BigDecimal limite = PayrollConstants.IRPF_LIMITS[i];
            BigDecimal limiteAnterior = i > 0 ? PayrollConstants.IRPF_LIMITS[i - 1] : BigDecimal.ZERO;

            if (baseCalculo.compareTo(limite) > 0) {
                BigDecimal valorTributavel = limite.subtract(limiteAnterior);
                totalIRRF = totalIRRF.add(valorTributavel.multiply(PayrollConstants.IRPF_RATES[i]));
                baseRestante = baseRestante.subtract(valorTributavel);
            } else {
                BigDecimal valorTributavel = baseCalculo.subtract(limiteAnterior);
                totalIRRF = totalIRRF.add(valorTributavel.multiply(PayrollConstants.IRPF_RATES[i]));
                break;
            }
        }

        if (baseRestante.compareTo(BigDecimal.ZERO) > 0) {
            totalIRRF = totalIRRF.add(baseRestante.multiply(PayrollConstants.IRPF_RATES[PayrollConstants.IRPF_RATES.length - 1]));
        }

        return totalIRRF.setScale(2, RoundingMode.HALF_UP);
    }
}
