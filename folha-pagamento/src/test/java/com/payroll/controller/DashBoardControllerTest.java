package com.payroll.controller;

import com.payroll.entity.Employee;
import com.payroll.entity.Payroll;
import com.payroll.service.EmployeeService;
import com.payroll.service.PayrollService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DashboardControllerTest {

    private DashboardController dashboardController;
    private EmployeeService employeeService;
    private PayrollService payrollService;

    @BeforeEach
    void setUp() {
        // Criar instâncias reais ou fake dos serviços com dados concretos

        employeeService = new EmployeeService() {
            @Override
            public List<Employee> getAllEmployees() {
                // Retorna lista concreta de employees, sem mock
                return List.of(
                    new Employee(1L, "João", "joao@email.com"),
                    new Employee(2L, "Maria", "maria@email.com")
                );
            }
        };

        payrollService = new PayrollService() {
            @Override
            public List<Payroll> getAllPayrolls() {
                // Retorna lista concreta de payrolls, sem mock
                return List.of(
                    new Payroll(1L, 1L, 3000),
                    new Payroll(2L, 2L, 4000)
                );
            }
        };

        dashboardController = new DashboardController();
        // Injetar serviços manuais (já que sem Spring no teste)
        dashboardController.employeeService = employeeService;
        dashboardController.payrollService = payrollService;
    }

    @Test
    void testGetDashboardData_withCurrentUser() {
        // Simular um currentUser
        UserDetails currentUser = new UserDetails() {
            @Override
            public String getUsername() {
                return "admin";
            }
            // outros métodos podem retornar default ou null
            @Override public List getAuthorities() { return null; }
            @Override public String getPassword() { return null; }
            @Override public boolean isAccountNonExpired() { return true; }
            @Override public boolean isAccountNonLocked() { return true; }
            @Override public boolean isCredentialsNonExpired() { return true; }
            @Override public boolean isEnabled() { return true; }
        };

        ResponseEntity<?> response = dashboardController.getDashboardData(currentUser);
        assertEquals(200, response.getStatusCodeValue());

        Map<String, Object> body = (Map<String, Object>) response.getBody();

        assertNotNull(body);
        assertEquals(2, body.get("totalEmployees"));
        assertEquals(2, body.get("totalPayrolls"));

        // Verificar lista de funcionários e folhas
        assertTrue(body.get("recentEmployees") instanceof List);
        assertTrue(body.get("recentPayrolls") instanceof List);

        assertEquals("admin", body.get("currentUser"));
    }

    @Test
    void testGetDashboardData_withoutCurrentUser() {
        ResponseEntity<?> response = dashboardController.getDashboardData(null);
        assertEquals(200, response.getStatusCodeValue());

        Map<String, Object> body = (Map<String, Object>) response.getBody();

        assertNotNull(body);
        assertEquals(2, body.get("totalEmployees"));
        assertEquals(2, body.get("totalPayrolls"));
        assertNull(body.get("currentUser"));
    }
}
