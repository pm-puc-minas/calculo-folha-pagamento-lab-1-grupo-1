package com.payroll.service;

import com.payroll.entity.Employee;
import com.payroll.entity.PayrollCalculation;
import com.payroll.repository.PayrollCalculationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PayrollServiceTest {

    @Autowired
    private PayrollCalculationRepository payrollRepository;

    private PayrollService payrollService;

    private Employee employee;

    @BeforeEach
    void setUp() throws Exception {
        payrollService = new PayrollService();

        // Injetando o repositório privado via Reflection
        Field repositoryField = PayrollService.class.getDeclaredField("payrollRepository");
        repositoryField.setAccessible(true);
        repositoryField.set(payrollService, payrollRepository);

        // Criando objeto Employee 
        employee = new Employee();
        employee.setId(1L);
        employee.setFullName("Bernardo");
        employee.setCpf("12345678900");
        employee.setRg("MG123456");
        employee.setPosition("Developer");
        employee.setAdmissionDate(LocalDate.of(2020, 1, 1));
        employee.setSalary(new BigDecimal("3000"));
        employee.setWeeklyHours(40);
        employee.setTransportVoucher(true);
        employee.setMealVoucher(true);
        employee.setMealVoucherValue(new BigDecimal("500"));
        employee.setDangerousWork(false);
        employee.setDangerousPercentage(BigDecimal.ZERO);
        employee.setUnhealthyWork(true);
        employee.setUnhealthyLevel("MEDIO");
        employee.setCreatedBy(1L);
    }

    @Test
    void testCalculatePayroll() {
        // Usando dados do objeto Employee
        PayrollCalculation calculation = payrollService.calculatePayroll(
                employee.getId(),
                "2025-10",
                employee.getCreatedBy()
        );

        assertNotNull(calculation.getId());
        assertEquals("2025-10", calculation.getReferenceMonth());
        assertEquals(employee.getCreatedBy(), calculation.getCreatedBy());
        assertEquals(new BigDecimal("3000.00"), calculation.getGrossSalary()
                .subtract(calculation.getDangerousBonus())
                .subtract(calculation.getUnhealthyBonus()));
    }

    @Test
    void testGetEmployeePayrolls() {
        payrollService.calculatePayroll(employee.getId(), "2025-10", employee.getCreatedBy());

        List<PayrollCalculation> payrolls = payrollService.getEmployeePayrolls(employee.getId());
        assertEquals(1, payrolls.size());
        assertEquals(employee.getId(), payrolls.get(0).getCreatedBy());
    }

    @Test
    void testGetAllPayrolls() {
        payrollService.calculatePayroll(employee.getId(), "2025-10", employee.getCreatedBy());

        List<PayrollCalculation> payrolls = payrollService.getAllPayrolls();
        assertTrue(payrolls.size() >= 1);
    }

    @Test
    void testRecalculateSameMonthReturnsExisting() {
        PayrollCalculation first = payrollService.calculatePayroll(employee.getId(), "2025-10", employee.getCreatedBy());
        PayrollCalculation second = payrollService.calculatePayroll(employee.getId(), "2025-10", employee.getCreatedBy());

        // O mesmo objeto deve ser retornado se já existe
        assertEquals(first.getId(), second.getId());
    }
}
