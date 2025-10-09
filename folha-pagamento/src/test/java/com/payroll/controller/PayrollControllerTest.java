package com.payroll.controller;

import com.payroll.entity.Employee;
import com.payroll.entity.PayrollCalculation;
import com.payroll.entity.User;
import com.payroll.service.EmployeeService;
import com.payroll.service.PayrollService;
import com.payroll.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para o PayrollController.
 *
 * Objetivo:
 * - Validar endpoints de listagem, cálculo e visualização de folhas de pagamento.
 * - Testar tratamento de exceções e comportamento com dados inexistentes.
 */
class PayrollControllerTest {

    @Mock
    private PayrollService payrollService;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private UserService userService;

    @InjectMocks
    private PayrollController payrollController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Testa se o endpoint /api/payroll retorna corretamente todas as folhas de pagamento.
     */
    @Test
    void payrollList_shouldReturnAllPayrolls() {
        PayrollCalculation p1 = new PayrollCalculation(); p1.setId(1L);
        PayrollCalculation p2 = new PayrollCalculation(); p2.setId(2L);
        List<PayrollCalculation> mockPayrolls = List.of(p1, p2);

        when(payrollService.getAllPayrolls()).thenReturn(mockPayrolls);

        ResponseEntity<List<PayrollCalculation>> response = payrollController.payrollList();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPayrolls, response.getBody());
    }

    /**
     * Testa se o endpoint /calculate cria uma folha de pagamento corretamente.
     */
    @Test
    void calculatePayroll_shouldReturnCreatedPayroll() {
        User user = new User(); user.setId(10L); user.setUsername("user123");
        PayrollCalculation calculation = new PayrollCalculation(); calculation.setId(100L);

        Map<String, String> request = Map.of(
                "employeeId", "1",
                "referenceMonth", "2025-10"
        );

        UserDetails currentUser = mock(UserDetails.class);
        when(currentUser.getUsername()).thenReturn("user123");

        when(userService.findByUsername("user123")).thenReturn(Optional.of(user));
        when(payrollService.calculatePayroll(1L, "2025-10", 10L)).thenReturn(calculation);

        ResponseEntity<?> response = payrollController.calculatePayroll(request, currentUser);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(calculation, response.getBody());
    }

    /**
     * Testa se o endpoint /calculate trata exceções corretamente.
     */
    @Test
    void calculatePayroll_shouldReturnInternalServerError_whenExceptionOccurs() {
        Map<String, String> request = Map.of(
                "employeeId", "1",
                "referenceMonth", "2025-10"
        );

        UserDetails currentUser = mock(UserDetails.class);
        when(currentUser.getUsername()).thenReturn("user123");
        when(userService.findByUsername("user123")).thenThrow(new RuntimeException("Falha"));

        ResponseEntity<?> response = payrollController.calculatePayroll(request, currentUser);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(((String) response.getBody()).contains("Falha"));
    }

    /**
     * Testa se o endpoint /{id} retorna a folha de pagamento correta quando existe.
     */
    @Test
    void viewPayroll_shouldReturnPayroll_whenExists() {
        PayrollCalculation p1 = new PayrollCalculation(); p1.setId(1L);
        List<PayrollCalculation> allPayrolls = List.of(p1);

        when(payrollService.getAllPayrolls()).thenReturn(allPayrolls);

        ResponseEntity<?> response = payrollController.viewPayroll(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(p1, response.getBody());
    }

    /**
     * Testa se o endpoint /{id} retorna 404 quando a folha de pagamento não existe.
     */
    @Test
    void viewPayroll_shouldReturnNotFound_whenDoesNotExist() {
        when(payrollService.getAllPayrolls()).thenReturn(List.of());

        ResponseEntity<?> response = payrollController.viewPayroll(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Folha de pagamento não encontrada", response.getBody());
    }

    /**
     * Testa se o endpoint /employee/{employeeId} retorna dados corretos para um funcionário existente.
     */
    @SuppressWarnings("unchecked")
    @Test
    void viewEmployeePayrolls_shouldReturnPayrollsForEmployee() {
        Employee emp = new Employee(); emp.setId(1L);
        PayrollCalculation p1 = new PayrollCalculation(); p1.setId(100L);
        List<PayrollCalculation> calculations = List.of(p1);

        when(employeeService.getEmployeeById(1L)).thenReturn(Optional.of(emp));
        when(payrollService.getEmployeePayrolls(1L)).thenReturn(calculations);

        ResponseEntity<?> response = payrollController.viewEmployeePayrolls(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(emp, body.get("employee"));
        assertEquals(calculations, body.get("calculations"));
    }

    /**
     * Testa se o endpoint /employee/{employeeId} retorna 404 quando o funcionário não existe.
     */
    @Test
    void viewEmployeePayrolls_shouldReturnNotFound_whenEmployeeDoesNotExist() {
        when(employeeService.getEmployeeById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = payrollController.viewEmployeePayrolls(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Funcionário não encontrado", response.getBody());
    }
}
