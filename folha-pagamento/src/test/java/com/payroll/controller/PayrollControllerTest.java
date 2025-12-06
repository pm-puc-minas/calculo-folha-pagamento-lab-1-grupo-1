package com.payroll.controller;

import com.payroll.entity.Employee;
import com.payroll.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PayrollControllerTest {

    @Autowired
    private PayrollController controller;

    @Autowired
    private EmployeeService employeeService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setCpf("12345678901");
        employee.setFullName("Bernardo Pereira");
        employee.setRg("MG1234567");
        employee.setPosition("Developer");
        employee.setSalary(BigDecimal.valueOf(3000.0));
        employee.setWeeklyHours(40);
        employee.setAdmissionDate(LocalDate.now());
        employee.setTransportVoucher(true);
        employee.setMealVoucher(true);
        employee.setMealVoucherValue(BigDecimal.valueOf(500));

        employee = employeeService.createEmployee(employee, 1L);
        assertNotNull(employee.getId());
    }

    @Test
    @DisplayName("Lista folhas vazia quando sistema sem calculos")
    void deveListarFolhasVazioInicialmente() {
        ResponseEntity<?> response = controller.payrollList();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((List<?>) response.getBody()).isEmpty());
    }

    @Test
    @DisplayName("Erro ao calcular com employeeId invalido")
    void deveFalharCalculoFolhaComEmployeeIdInvalido() {
        Map<String, String> request = new HashMap<>();
        request.put("employeeId", "abc");
        request.put("referenceMonth", "2025-10");

        ResponseEntity<?> response = controller.calculatePayroll(request, null);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Erro ao calcular folha"));
    }

    @Test
    @DisplayName("Folha inexistente retorna 404")
    void deveRetornar404AoVisualizarFolhaInexistente() {
        ResponseEntity<?> response = controller.viewPayroll(999L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Folha de pagamento nao encontrada", response.getBody());
    }

    @Test
    @DisplayName("Folhas por funcionario inexistente retornam 404")
    void deveRetornar404AoListarFolhasDeFuncionarioInexistente() {
        ResponseEntity<?> response = controller.viewEmployeePayrolls(999L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Funcionario nao encontrado", response.getBody());
    }
}
