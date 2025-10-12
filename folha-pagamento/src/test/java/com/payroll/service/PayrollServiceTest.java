package com.payroll.service;

import com.payroll.entity.Employee;
import com.payroll.entity.PayrollCalculation;
import com.payroll.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PayrollServiceTest {

    @Autowired
    private PayrollService payrollService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;

    @BeforeEach
    void setUp() {
        // Criar Employee real
        employee = new Employee();
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

        // Salvar Employee usando EmployeeService real
        employee = employeeService.createEmployee(employee, 1L);
    }

    @Test
    void testCalculatePayroll() {
        PayrollCalculation calculation = payrollService.calculatePayroll(
                employee.getId(),
                "2025-10",
                employee.getCreatedBy()
        );

        assertNotNull(calculation.getId());
        assertEquals("2025-10", calculation.getReferenceMonth());
        assertEquals(employee.getId(), calculation.getEmployee().getId());
        assertEquals(employee.getCreatedBy(), calculation.getCreatedBy());
    }

    @Test
    void testGetEmployeePayrolls() {
        payrollService.calculatePayroll(employee.getId(), "2025-10", employee.getCreatedBy());

        List<PayrollCalculation> payrolls = payrollService.getEmployeePayrolls(employee.getId());
        assertEquals(1, payrolls.size());
        assertEquals(employee.getId(), payrolls.get(0).getEmployee().getId());
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
