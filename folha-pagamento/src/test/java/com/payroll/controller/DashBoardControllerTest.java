package com.payroll.controller;

import com.payroll.entity.Employee;
import com.payroll.entity.PayrollCalculation;
import com.payroll.service.EmployeeService;
import com.payroll.service.PayrollService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DashboardControllerTest {

    @Mock
    private EmployeeService employeeService;

    @Mock
    private PayrollService payrollService;

    @InjectMocks
    private DashboardController dashboardController;

    private Employee employee1;
    private Employee employee2;
    private PayrollCalculation payroll1;
    private PayrollCalculation payroll2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employee1 = new Employee();
        employee1.setId(1L);
        employee2 = new Employee();
        employee2.setId(2L);
        payroll1 = new PayrollCalculation();
        payroll1.setId(1L);
        payroll2 = new PayrollCalculation();
        payroll2.setId(2L);
         
    }

    @Test
    void testGetDashboardData_withUser() {
        when(employeeService.getAllEmployees()).thenReturn(List.of(employee1, employee2));
        when(payrollService.getAllPayrolls()).thenReturn(List.of(payroll1, payroll2));

        UserDetails user = User.withUsername("admin").password("password").roles("ADMIN").build();
        ResponseEntity<?> response = dashboardController.getDashboardData(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) response.getBody();
        assertNotNull(data);

        assertEquals(2, data.get("totalEmployees"));
        assertEquals(2, data.get("totalPayrolls"));
        assertEquals("admin", data.get("currentUser"));

        @SuppressWarnings("unchecked")
        List<Employee> recentEmployees = (List<Employee>) data.get("recentEmployees");
        @SuppressWarnings("unchecked")
        List<PayrollCalculation> recentPayrolls = (List<PayrollCalculation>) data.get("recentPayrolls");

        assertEquals(2, recentEmployees.size());
        assertEquals(2, recentPayrolls.size());
    }

    @Test
    void testGetDashboardData_withoutUser() {
        when(employeeService.getAllEmployees()).thenReturn(List.of(employee1, employee2));
        when(payrollService.getAllPayrolls()).thenReturn(List.of(payroll1, payroll2));

        ResponseEntity<?> response = dashboardController.getDashboardData(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) response.getBody();
        assertNotNull(data);

        assertNull(data.get("currentUser"));
        assertEquals(2, data.get("totalEmployees"));
        assertEquals(2, data.get("totalPayrolls"));
    }

    @Test
    void testGetDashboardData_handlesEmployeeServiceException() {
        when(employeeService.getAllEmployees()).thenThrow(new RuntimeException("Erro forçado"));

        ResponseEntity<?> response = dashboardController.getDashboardData(null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> data = (Map<String, String>) response.getBody();
        assertNotNull(data);
        assertTrue(data.get("error").contains("Erro ao carregar dados do dashboard"));
    }

    @Test
    void testGetDashboardData_handlesPayrollServiceException() {
        when(employeeService.getAllEmployees()).thenReturn(List.of(employee1, employee2));
        when(payrollService.getAllPayrolls()).thenThrow(new RuntimeException("Erro na folha"));

        ResponseEntity<?> response = dashboardController.getDashboardData(null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> data = (Map<String, String>) response.getBody();
        assertNotNull(data);
        assertTrue(data.get("error").contains("Erro ao carregar dados do dashboard"));
    }
}
