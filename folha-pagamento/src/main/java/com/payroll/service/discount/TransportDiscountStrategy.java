package com.payroll.service.discount;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

import com.payroll.service.PayrollConstants;

@Component
public class TransportDiscountStrategy implements DiscountStrategy {

    @Override
    public DiscountType getType() {
        return DiscountType.TRANSPORT;
    }

    @Override
    public BigDecimal calculate(DiscountCalculationContext context) {
        if (!context.isTransportEnabled()) {
            return BigDecimal.ZERO;
        }

        BigDecimal salarioBruto = context.getGrossSalary();
        BigDecimal valorEntregue = context.getTransportVoucherValue();
        if (salarioBruto == null || valorEntregue == null) return BigDecimal.ZERO;

        BigDecimal descontoMaximo = salarioBruto.multiply(PayrollConstants.TRANSPORTE_RATE);
        return valorEntregue.min(descontoMaximo).setScale(2, RoundingMode.HALF_UP);
    }
}
