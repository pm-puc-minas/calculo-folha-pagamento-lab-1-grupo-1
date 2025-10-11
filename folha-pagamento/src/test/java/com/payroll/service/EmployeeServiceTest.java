package com.payroll.service;

import com.payroll.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EmployeeServiceTest {

    

    private EmployeeService employeeService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService();
        

        // Criando objeto Employee real // no employee
        employee = new Employee();
        employee.setFullName("Bernardo"); // no employee
        employee.setCpf("12345678900"); // no employee
        employee.setRg("MG123456"); // no employee
        employee.setPosition("Developer"); // no employee
        employee.setAdmissionDate(LocalDate.of(2020, 1, 1)); // no employee
        employee.setSalary(new BigDecimal("5000")); // no employee
        employee.setWeeklyHours(40); // no employee
        employee.setTransportVoucher(true); // no employee
        employee.setMealVoucher(true); // no employee
        employee.setMealVoucherValue(new BigDecimal("500")); // no employee
        employee.setDangerousWork(false); // no employee
        employee.setDangerousPercentage(BigDecimal.ZERO); // no employee
        employee.setUnhealthyWork(false); // no employee
        employee.setUnhealthyLevel(null); // no employee
    }

    @Test
    void testCreateEmployee() {
        Employee created = employeeService.createEmployee(employee, 1L); // no employee
        assertNotNull(created.getId()); // no employee
        assertEquals("Bernardo", created.getFullName()); // no employee
        assertEquals(1L, created.getCreatedBy()); // no employee
    }

    @Test
    void testGetAllEmployees() {
        employeeService.createEmployee(employee, 1L); // no employee
        List<Employee> employees = employeeService.getAllEmployees();
        assertEquals(1, employees.size());
        assertEquals("Bernardo", employees.get(0).getFullName()); // no employee
    }

    @Test
    void testGetEmployeeById() {
        Employee created = employeeService.createEmployee(employee, 1L); // no employee
        Optional<Employee> result = employeeService.getEmployeeById(created.getId());
        assertTrue(result.isPresent());
        assertEquals("Bernardo", result.get().getFullName()); // no employee
    }

    @Test
    void testGetEmployeeByCpf() {
        employeeService.createEmployee(employee, 1L); // no employee
        Optional<Employee> result = employeeService.getEmployeeByCpf("12345678900"); // no employee
        assertTrue(result.isPresent());
        assertEquals("Bernardo", result.get().getFullName()); // no employee
    }

    @Test
    void testExistsByCpf() {
        employeeService.createEmployee(employee, 1L); // no employee
        boolean exists = employeeService.existsByCpf("12345678900"); // no employee
        assertTrue(exists);
    }

    @Test
    void testUpdateEmployee() {
        Employee created = employeeService.createEmployee(employee, 1L); // no employee

        Employee updatedDetails = new Employee();
        updatedDetails.setFullName("Gustavo"); // no employee
        updatedDetails.setCpf("09876543211"); // no employee
        updatedDetails.setRg("MG654321"); // no employee
        updatedDetails.setPosition("Tester"); // no employee
        updatedDetails.setAdmissionDate(LocalDate.of(2021, 2, 1)); // no employee
        updatedDetails.setSalary(new BigDecimal("6000")); // no employee
        updatedDetails.setWeeklyHours(35); // no employee
        updatedDetails.setTransportVoucher(false); // no employee
        updatedDetails.setMealVoucher(false); // no employee
        updatedDetails.setMealVoucherValue(BigDecimal.ZERO); // no employee
        updatedDetails.setDangerousWork(true); // no employee
        updatedDetails.setDangerousPercentage(new BigDecimal("10")); // no employee
        updatedDetails.setUnhealthyWork(true); // no employee
        updatedDetails.setUnhealthyLevel("ALTO"); // no employee

        Employee result = employeeService.updateEmployee(created.getId(), updatedDetails); // no employee

        assertEquals("Gustavo", result.getFullName()); // no employee
        assertEquals("09876543211", result.getCpf()); // no employee
        assertTrue(result.getDangerousWork()); // no employee
    }

    @Test
    void testDeleteEmployee() {
        Employee created = employeeService.createEmployee(employee, 1L); // no employee
        employeeService.deleteEmployee(created.getId()); // no employee
        Optional<Employee> result = employeeService.getEmployeeById(created.getId());
        assertFalse(result.isPresent()); // no employee
    }
}
