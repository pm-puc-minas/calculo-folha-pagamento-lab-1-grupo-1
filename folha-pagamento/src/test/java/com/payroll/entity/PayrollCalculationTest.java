package com.payroll.entity;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários mínimos para a entidade PayrollCalculation.
 * Objetivo:
 * - Garantir que a criação de um PayrollCalculation e o uso de getters/setters funciona corretamente.
 */
class PayrollCalculationTest {

    /**
     * Testa a criação de um PayrollCalculation e atribuição de valores aos campos.
     */
    @Test
    void payrollCalculationCreationAndGettersSetters() {
        PayrollCalculation calc = new PayrollCalculation();

        Employee emp = new Employee();
        emp.setFullName("João Silva");
        calc.setEmployee(emp);

        calc.setId(1L);
        calc.setReferenceMonth("2025-10");
        calc.setGrossSalary(new BigDecimal("5000.00"));
        calc.setNetSalary(new BigDecimal("4000.00"));
        calc.setInssDiscount(new BigDecimal("500.00"));
        calc.setIrpfDiscount(new BigDecimal("500.00"));
        calc.setTransportDiscount(new BigDecimal("100.00"));
        calc.setFgtsValue(new BigDecimal("400.00"));
        calc.setDangerousBonus(new BigDecimal("50.00"));
        calc.setUnhealthyBonus(new BigDecimal("30.00"));
        calc.setMealVoucherValue(new BigDecimal("200.00"));
        calc.setHourlyWage(new BigDecimal("31.25"));
        calc.setCreatedBy(99L);

        assertEquals(1L, calc.getId());
        assertEquals("João Silva", calc.getEmployee().getFullName());
        assertEquals("2025-10", calc.getReferenceMonth());
        assertEquals(new BigDecimal("5000.00"), calc.getGrossSalary());
        assertEquals(new BigDecimal("4000.00"), calc.getNetSalary());
        assertEquals(new BigDecimal("500.00"), calc.getInssDiscount());
        assertEquals(new BigDecimal("500.00"), calc.getIrpfDiscount());
        assertEquals(new BigDecimal("100.00"), calc.getTransportDiscount());
        assertEquals(new BigDecimal("400.00"), calc.getFgtsValue());
        assertEquals(new BigDecimal("50.00"), calc.getDangerousBonus());
        assertEquals(new BigDecimal("30.00"), calc.getUnhealthyBonus());
        assertEquals(new BigDecimal("200.00"), calc.getMealVoucherValue());
        assertEquals(new BigDecimal("31.25"), calc.getHourlyWage());
        assertEquals(99L, calc.getCreatedBy());
        assertNotNull(calc.getCreatedAt()); // criado no construtor
    }
}
