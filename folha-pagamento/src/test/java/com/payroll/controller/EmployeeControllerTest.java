package com.payroll.controller;

import com.payroll.entity.Employee;
import com.payroll.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeControllerTest {

    private EmployeeController employeeController;
    private EmployeeService employeeService;
    @BeforeEach

    void setUp() {
            employeeService = new EmployeeService(); // instância real do service
            employeeController = new EmployeeController(); 
    }


    @Test
    void testListEmployees() {
        ResponseEntity<?> response = employeeController.listEmployees();
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void testCreateEmployee() {
        Employee employee = new Employee();
        // preencher com campos existentes no seu Employee
        employee.setCpf("12345678901");
        employee.setPosition("Developer");

        ResponseEntity<?> response = employeeController.createEmployee(employee, null);
        assertTrue(response.getStatusCode().value() == 201 || response.getStatusCode().value() == 409);
    }

    @Test
    void testViewEmployee() {
        Long employeeId = 1L;
        ResponseEntity<?> response = employeeController.viewEmployee(employeeId);
        assertTrue(response.getStatusCode().value() == 200 || response.getStatusCode().value() == 404);
    }

    @Test
    void testUpdateEmployee() {
        Employee employee = new Employee();
        employee.setCpf("12345678901");
        employee.setPosition("Developer");

        Long employeeId = 1L;
        
        ResponseEntity<?> response = employeeController.updateEmployee(employeeId, employee);
        assertTrue(response.getStatusCode().value() == 200 || response.getStatusCode().value() == 404);
    }

    @Test
    void testDeleteEmployee() {
        Long employeeId = 1L;
        ResponseEntity<?> response = employeeController.deleteEmployee(employeeId);
        assertTrue(response.getStatusCode().value() == 200 || response.getStatusCode().value() == 404);
    }
}
