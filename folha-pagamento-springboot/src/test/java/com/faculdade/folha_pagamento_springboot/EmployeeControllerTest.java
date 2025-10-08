package com.payroll.controller;

import com.payroll.entity.Employee;
import com.payroll.entity.PayrollCalculation;
import com.payroll.service.EmployeeService;
import com.payroll.service.PayrollService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para o DashboardController.
 *
 * Objetivo:
 * - Verificar se o endpoint do dashboard retorna os dados corretos.
 * - Testar comportamento com usuário autenticado e não autenticado.
 * - Testar tratamento de exceção nos serviços.
 */
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @Mock
    private PayrollService payrollService;

    @InjectMocks
    private DashboardController dashboardController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Testa se o endpoint retorna corretamente os dados do dashboard
     * quando os serviços retornam dados válidos e o usuário está autenticado.
     */
    @SuppressWarnings("unchecked")
    @Test
    void getDashboardData_shouldReturnDashboard_whenServicesReturnData() {
        // Criando mocks de Employees com id (sem setName)
        Employee emp1 = new Employee(); emp1.setId(1L);
        Employee emp2 = new Employee(); emp2.setId(2L);
        List<Employee> mockEmployees = List.of(emp1, emp2);

        // Criando mocks de PayrollCalculations
        PayrollCalculation payroll1 = new PayrollCalculation(); payroll1.setId(1L);
        PayrollCalculation payroll2 = new PayrollCalculation(); payroll2.setId(2L);
        List<PayrollCalculation> mockPayrolls = List.of(payroll1, payroll2);

        // Configuração dos serviços
        when(employeeService.getAllEmployees()).thenReturn(mockEmployees);
        when(payrollService.getAllPayrolls()).thenReturn(mockPayrolls);

        // Usuário autenticado
        UserDetails currentUser = mock(UserDetails.class);
        when(currentUser.getUsername()).thenReturn("user123");

        ResponseEntity<?> response = dashboardController.getDashboardData(currentUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(2, body.get("totalEmployees"));
        assertEquals(2, body.get("totalPayrolls"));
        assertEquals(mockEmployees, body.get("recentEmployees"));
        assertEquals(mockPayrolls, body.get("recentPayrolls"));
        assertEquals("user123", body.get("currentUser"));
    }

    /**
     * Testa se o endpoint retorna corretamente os dados do dashboard
     * quando o usuário não está autenticado (currentUser = null).
     */
    @SuppressWarnings("unchecked")
    @Test
    void getDashboardData_shouldReturnNullCurrentUser_whenNotAuthenticated() {
        Employee emp1 = new Employee(); emp1.setId(1L);
        List<Employee> mockEmployees = List.of(emp1);

        PayrollCalculation payroll1 = new PayrollCalculation(); payroll1.setId(1L);
        List<PayrollCalculation> mockPayrolls = List.of(payroll1);

        when(employeeService.getAllEmployees()).thenReturn(mockEmployees);
        when(payrollService.getAllPayrolls()).thenReturn(mockPayrolls);

        ResponseEntity<?> response = dashboardController.getDashboardData(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertNull(body.get("currentUser"));
    }

    /**
     * Testa se o endpoint retorna status 500 e mensagem de erro
     * quando algum serviço lança uma exceção.
     */
    @SuppressWarnings("unchecked")
    @Test
    void getDashboardData_shouldReturn500_whenServiceThrowsException() {
        when(employeeService.getAllEmployees()).thenThrow(new RuntimeException("Falha"));
        when(payrollService.getAllPayrolls()).thenReturn(List.of());

        UserDetails currentUser = mock(UserDetails.class);
        when(currentUser.getUsername()).thenReturn("user123");

        ResponseEntity<?> response = dashboardController.getDashboardData(currentUser);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("error"));
        assertTrue(((String) body.get("error")).contains("Falha"));
    }
}
