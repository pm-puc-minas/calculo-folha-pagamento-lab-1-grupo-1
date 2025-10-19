package com.payroll.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/*Garantir que a criação de um Employee e o uso de getters/setters funciona corretamente.*/
class EmployeeTest {

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setFullName("João Silva");
        employee.setCpf("12345678900");
        employee.setRg("MG1234567");
        employee.setPosition("Analista");
        employee.setAdmissionDate(LocalDate.of(2023, 1, 10));
        employee.setSalary(new BigDecimal("3500.00"));
        employee.setWeeklyHours(40);
        employee.setTransportVoucher(true);
        employee.setMealVoucher(true);
        employee.setMealVoucherValue(new BigDecimal("500.00"));
        employee.setDangerousWork(false);
        employee.setDangerousPercentage(BigDecimal.ZERO);
        employee.setUnhealthyWork(false);
        employee.setUnhealthyLevel(null);
        employee.setCreatedBy(99L);
    }

    @Test
    @DisplayName("Getters e setters de Employee funcionam")
    // Valida getters e setters da entidade Employee
    void deveValidarGettersESetters() {
        assertEquals(1L, employee.getId());
        assertEquals("João Silva", employee.getFullName());
        assertEquals("12345678900", employee.getCpf());
        assertEquals("MG1234567", employee.getRg());
        assertEquals("Analista", employee.getPosition());
        assertEquals(LocalDate.of(2023, 1, 10), employee.getAdmissionDate());
        assertEquals(new BigDecimal("3500.00"), employee.getSalary());
        assertEquals(40, employee.getWeeklyHours());
        assertTrue(employee.getTransportVoucher());
        assertTrue(employee.getMealVoucher());
        assertEquals(new BigDecimal("500.00"), employee.getMealVoucherValue());
        assertFalse(employee.getDangerousWork());
        assertEquals(BigDecimal.ZERO, employee.getDangerousPercentage());
        assertFalse(employee.getUnhealthyWork());
        assertNull(employee.getUnhealthyLevel());
        assertEquals(99L, employee.getCreatedBy());
        assertNotNull(employee.getCreatedAt()); // criado no construtor
    }
}
