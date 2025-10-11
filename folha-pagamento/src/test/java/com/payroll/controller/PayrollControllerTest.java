package com.payroll.controller;


import com.payroll.service.EmployeeService;
import com.payroll.service.PayrollService;
import com.payroll.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PayrollControllerTest {

    private PayrollController controller;
    private PayrollService payrollService;
    private EmployeeService employeeService;
    private UserService userService;

    @BeforeEach
    void setup() throws Exception {
        payrollService = new PayrollService();
        employeeService = new EmployeeService();
        userService = new UserService();

        controller = new PayrollController();

        // Injetar serviços via Reflection
        java.lang.reflect.Field payrollField = PayrollController.class.getDeclaredField("payrollService");
        payrollField.setAccessible(true);
        payrollField.set(controller, payrollService);

        java.lang.reflect.Field employeeField = PayrollController.class.getDeclaredField("employeeService");
        employeeField.setAccessible(true);
        employeeField.set(controller, employeeService);

        java.lang.reflect.Field userField = PayrollController.class.getDeclaredField("userService");
        userField.setAccessible(true);
        userField.set(controller, userService);
    }

    @Test
    void testPayrollListEmpty() {
        ResponseEntity<?> response = controller.payrollList();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((List<?>) response.getBody()).isEmpty());
    }

    @Test
    void testCalculatePayrollError() {
        Map<String, String> request = new HashMap<>();
        request.put("employeeId", "abc"); // inválido
        request.put("referenceMonth", "2025-10");

        // Usando null como UserDetails para não chamar lambda
        ResponseEntity<?> response = controller.calculatePayroll(request, null);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Erro ao calcular folha"));
    }

    @Test
    void testViewPayrollNotFound() {
        ResponseEntity<?> response = controller.viewPayroll(999L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Folha de pagamento não encontrada", response.getBody());
    }

    @Test
    void testViewEmployeePayrollsNotFound() {
        ResponseEntity<?> response = controller.viewEmployeePayrolls(999L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Funcionário não encontrado", response.getBody());
    }
}
