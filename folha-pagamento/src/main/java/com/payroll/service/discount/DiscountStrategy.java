package com.payroll.service.discount;

import java.math.BigDecimal;

public interface DiscountStrategy {
    DiscountType getType();
    BigDecimal calculate(DiscountCalculationContext context);
}
