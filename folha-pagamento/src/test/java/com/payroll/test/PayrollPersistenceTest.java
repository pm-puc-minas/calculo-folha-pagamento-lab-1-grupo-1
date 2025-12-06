package com.payroll.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.payroll.entity.Employee;
import com.payroll.entity.PayrollCalculation;
import com.payroll.repository.EmployeeRepository;
import com.payroll.repository.PayrollCalculationRepository;

@DataJpaTest
public class PayrollPersistenceTest {

    @Autowired
    private PayrollCalculationRepository payrollCalculationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void testSalvarERecuperarPayrollCalculation() {
        Employee employee = new Employee();
        employee.setFullName("Joao Teste");
        employee.setCpf("99988877766");
        employee.setRg("MG1234567");
        employee.setPosition("Analista");
        employee.setAdmissionDate(LocalDate.of(2022, 1, 10));
        employee.setSalary(new BigDecimal("3500.00"));
        employee.setWeeklyHours(40);
        Employee savedEmployee = employeeRepository.save(employee);

        PayrollCalculation calc = new PayrollCalculation();
        calc.setEmployee(savedEmployee);
        calc.setReferenceMonth("11/2025");
        calc.setGrossSalary(new BigDecimal("4500.00"));
        calc.setNetSalary(new BigDecimal("3800.00"));
        calc.setInssDiscount(new BigDecimal("500.00"));
        calc.setIrpfDiscount(new BigDecimal("150.00"));
        calc.setTransportDiscount(new BigDecimal("50.00"));
        calc.setFgtsValue(new BigDecimal("360.00"));
        calc.setDangerousBonus(new BigDecimal("200.00"));
        calc.setUnhealthyBonus(new BigDecimal("120.00"));
        calc.setMealVoucherValue(new BigDecimal("300.00"));
        calc.setHourlyWage(new BigDecimal("25.50"));
        calc.setCreatedAt(LocalDateTime.now());
        calc.setCreatedBy(1L);

        PayrollCalculation salvo = payrollCalculationRepository.save(calc);
        assertThat(salvo.getId()).isNotNull();

        PayrollCalculation recuperado = payrollCalculationRepository.findById(salvo.getId()).orElse(null);
        assertThat(recuperado).isNotNull();
        assertThat(recuperado.getEmployee().getId()).isEqualTo(savedEmployee.getId());
        assertThat(recuperado.getReferenceMonth()).isEqualTo("11/2025");
        assertThat(recuperado.getNetSalary()).isEqualByComparingTo(new BigDecimal("3800.00"));
    }
}
