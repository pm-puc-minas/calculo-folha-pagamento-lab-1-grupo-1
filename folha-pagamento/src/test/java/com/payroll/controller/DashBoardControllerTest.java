package com.payroll.controller;

import com.payroll.entity.Employee;
import com.payroll.entity.User;
import com.payroll.service.EmployeeService;
import com.payroll.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeControllerTest {

    private EmployeeController employeeController;
    private EmployeeService employeeService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        
        employeeService = new EmployeeService() {
            private final List<Employee> employees = List.of(
                new Employee(1L, "12345678901", "João"),
                new Employee(2L, "10987654321", "Maria")
            );

            @Override
            public List<Employee> getAllEmployees() {
                return employees;
            }

            @Override
            public boolean existsByCpf(String cpf) {
                return employees.stream().anyMatch(e -> e.getCpf().equals(cpf));
            }

            @Override
            public void createEmployee(Employee employee, Long userId) {
                // Simula criação (não faz nada)
            }

            @Override
            public Optional<Employee> getEmployeeById(Long id) {
                return employees.stream().filter(e -> e.getId().equals(id)).findFirst();
            }

            @Override
            public void updateEmployee(Long id, Employee employee) {
                // Simula atualização (não faz nada)
            }

            @Override
            public void deleteEmployee(Long id) {
                // Simula exclusão (não faz nada)
            }
        };

        userService = new UserService() {
            private final List<User> users = List.of(
                new User(1L, "admin", "senha123", null)
            );

            @Override
            public Optional<User> findByUsername(String username) {
                return users.stream().filter(u -> u.getUsername().equals(username)).findFirst();
            }
        };

        employeeController = new EmployeeController();
        employeeController.employeeService = employeeService;
        employeeController.userService = userService;
    }

    @Test
    void testListEmployees() {
        ResponseEntity<List<Employee>> response = employeeController.listEmployees();
        assertEquals(200, response.getStatusCodeValue());
        List<Employee> employees = response.getBody();
        assertNotNull(employees);
        assertEquals(2, employees.size());
    }

    @Test
    void testCreateEmployee_success() {
        Employee newEmployee = new Employee(null, "55566677788", "Carlos");

        UserDetails currentUser = new UserDetails() {
            @Override public String getUsername() { return "admin"; }
            @Override public List getAuthorities() { return null; }
            @Override public String getPassword() { return null; }
            @Override public boolean isAccountNonExpired() { return true; }
            @Override public boolean isAccountNonLocked() { return true; }
            @Override public boolean isCredentialsNonExpired() { return true; }
            @Override public boolean isEnabled() { return true; }
        };

        ResponseEntity<?> response = employeeController.createEmployee(newEmployee, currentUser);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(newEmployee, response.getBody());
    }

    @Test
    void testCreateEmployee_conflictCpf() {
        Employee newEmployee = new Employee(null, "12345678901", "Carlos"); // CPF já existe

        UserDetails currentUser = new UserDetails() {
            @Override public String getUsername() { return "admin"; }
            @Override public List getAuthorities() { return null; }
            @Override public String getPassword() { return null; }
            @Override public boolean isAccountNonExpired() { return true; }
            @Override public boolean isAccountNonLocked() { return true; }
            @Override public boolean isCredentialsNonExpired() { return true; }
            @Override public boolean isEnabled() { return true; }
        };

        ResponseEntity<?> response = employeeController.createEmployee(newEmployee, currentUser);
        assertEquals(409, response.getStatusCodeValue());
        assertEquals("CPF já cadastrado", response.getBody());
    }

    @Test
    void testViewEmployee_found() {
        ResponseEntity<?> response = employeeController.viewEmployee(1L);
        assertEquals(200, response.getStatusCodeValue());
        Employee employee = (Employee) response.getBody();
        assertNotNull(employee);
        assertEquals(1L, employee.getId());
    }

    @Test
    void testViewEmployee_notFound() {
        ResponseEntity<?> response = employeeController.viewEmployee(999L);
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Funcionário não encontrado", response.getBody());
    }

    @Test
    void testUpdateEmployee_success() {
        Employee updatedEmployee = new Employee(1L, "12345678901", "João Atualizado");
        ResponseEntity<?> response = employeeController.updateEmployee(1L, updatedEmployee);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedEmployee, response.getBody());
    }

    @Test
    void testDeleteEmployee_success() {
        ResponseEntity<?> response = employeeController.deleteEmployee(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Funcionário excluído com sucesso", response.getBody());
    }
}
