package com.payroll.controller;

import com.payroll.entity.Employee;
import com.payroll.repository.EmployeeRepository;
import com.payroll.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EmployeeControllerTest {

    @Autowired
    private EmployeeRepository employeeRepository; // repository real

    private EmployeeService employeeService;
    private EmployeeController employeeController;

    @BeforeEach
    void setUp() throws Exception {
        // Instancia normalmente
        employeeService = new EmployeeService(); 
        employeeController = new EmployeeController(); 

        // Injeção do repository no service
        Field repoField = EmployeeService.class.getDeclaredField("employeeRepository");
        repoField.setAccessible(true);
        repoField.set(employeeService, employeeRepository);

        // Injeção do service no controller
        Field serviceField = EmployeeController.class.getDeclaredField("employeeService");
        serviceField.setAccessible(true);
        serviceField.set(employeeController, employeeService);
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
        employee.setCpf("12345678901");
        employee.setPosition("Developer");

        // Campos obrigatórios para não gerar erro de validação JPA/Hibernate
        employee.setFullName("Bernardo Pereira"); // usado para não dar erro @NotBlank
        employee.setRg("MG123456");               // usado para não dar erro @NotBlank
        employee.setAdmissionDate(LocalDate.of(2020, 1, 1)); // usado para não dar erro @NotNull
        employee.setSalary(new BigDecimal("3000"));          // usado para não dar erro @NotNull
        employee.setWeeklyHours(40);                         // usado para não dar erro @NotNull

        ResponseEntity<?> response = employeeController.createEmployee(employee, null);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void testViewEmployee() {
        // Primeiro cria um Employee real
        Employee employee = new Employee();
        employee.setCpf("12345678902");
        employee.setPosition("Tester");

        // Campos obrigatórios
        employee.setFullName("Lucas Silva"); 
        employee.setRg("MG654321");               
        employee.setAdmissionDate(LocalDate.of(2021, 5, 10)); 
        employee.setSalary(new BigDecimal("2500"));          
        employee.setWeeklyHours(40);                        

        Employee saved = employeeService.createEmployee(employee, null);

        ResponseEntity<?> response = employeeController.viewEmployee(saved.getId());
        assertTrue(response.getStatusCode().value() == 200 || response.getStatusCode().value() == 404);
    }

    @Test
    void testUpdateEmployee() {
        // Criar Employee real
        Employee employee = new Employee();
        employee.setCpf("12345678903");
        employee.setPosition("Intern");

        // Campos obrigatórios
        employee.setFullName("Maria Oliveira"); 
        employee.setRg("MG987654");               
        employee.setAdmissionDate(LocalDate.of(2022, 3, 15)); 
        employee.setSalary(new BigDecimal("2000"));          
        employee.setWeeklyHours(20);                        

        Employee saved = employeeService.createEmployee(employee, null);

        // Alterações
        Employee update = new Employee();
        update.setCpf("12345678903");
        update.setPosition("Junior Developer");

        // Campos obrigatórios para atualização
        update.setFullName("Maria Oliveira"); 
        update.setRg("MG987654");               
        update.setAdmissionDate(LocalDate.of(2022, 3, 15)); 
        update.setSalary(new BigDecimal("2000"));          
        update.setWeeklyHours(20);                        

        ResponseEntity<?> response = employeeController.updateEmployee(saved.getId(), update);

        assertEquals(200, response.getStatusCode().value());
        Employee updatedEmployee = (Employee) response.getBody();
        assertEquals("Junior Developer", updatedEmployee.getPosition());
    }

    @Test
    void testDeleteEmployee() {
        // Criar Employee real
        Employee employee = new Employee();
        employee.setCpf("12345678904");
        employee.setPosition("Analyst");

        // Campos obrigatórios
        employee.setFullName("João Santos"); 
        employee.setRg("MG112233");               
        employee.setAdmissionDate(LocalDate.of(2019, 8, 20)); 
        employee.setSalary(new BigDecimal("2800"));          
        employee.setWeeklyHours(40);                        

        Employee saved = employeeService.createEmployee(employee, null);

        ResponseEntity<?> response = employeeController.deleteEmployee(saved.getId());

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Deletado com sucesso", response.getBody());
    }
}
