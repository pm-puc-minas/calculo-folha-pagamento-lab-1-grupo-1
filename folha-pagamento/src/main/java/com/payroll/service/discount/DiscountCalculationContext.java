package com.payroll.service.discount;

import java.math.BigDecimal;

/**
 * Contexto compartilhado para estratégias de desconto da folha.
 */
public class DiscountCalculationContext {
    private BigDecimal grossSalary;
    private BigDecimal inssDiscount;
    private int dependents;
    private BigDecimal transportVoucherValue;
    private boolean transportEnabled;
    private BigDecimal pensionAlimony;

    public BigDecimal getGrossSalary() {
        return grossSalary;
    }

    public DiscountCalculationContext setGrossSalary(BigDecimal grossSalary) {
        this.grossSalary = grossSalary;
        return this;
    }

    public BigDecimal getInssDiscount() {
        return inssDiscount;
    }

    public DiscountCalculationContext setInssDiscount(BigDecimal inssDiscount) {
        this.inssDiscount = inssDiscount;
        return this;
    }

    public int getDependents() {
        return dependents;
    }

    public DiscountCalculationContext setDependents(int dependents) {
        this.dependents = dependents;
        return this;
    }

    public BigDecimal getTransportVoucherValue() {
        return transportVoucherValue;
    }

    public DiscountCalculationContext setTransportVoucherValue(BigDecimal transportVoucherValue) {
        this.transportVoucherValue = transportVoucherValue;
        return this;
    }

    public boolean isTransportEnabled() {
        return transportEnabled;
    }

    public DiscountCalculationContext setTransportEnabled(boolean transportEnabled) {
        this.transportEnabled = transportEnabled;
        return this;
    }

    public BigDecimal getPensionAlimony() {
        return pensionAlimony;
    }

    public DiscountCalculationContext setPensionAlimony(BigDecimal pensionAlimony) {
        this.pensionAlimony = pensionAlimony;
        return this;
    }
}
