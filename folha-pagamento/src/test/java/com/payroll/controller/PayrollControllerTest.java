package com.payroll.controller;

import com.payroll.dtos.payroll.PayrollCalculationRequestDTO; // NOVO: Importação do DTO de requisição
import com.payroll.entity.Employee;
import com.payroll.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de teste de integração para PayrollController, utilizando SpringBootTest
 * e um banco de dados transacional.
 */
@SpringBootTest
@Transactional
class PayrollControllerTest {

    @Autowired
    private PayrollController controller;

    @Autowired
    private EmployeeService employeeService;

    private Employee employee;

    /**
     * Configura o ambiente de teste antes de cada método,
     * garantindo que haja um Employee válido no banco para os testes de folha de pagamento.
     */
    @BeforeEach
    void setUp() {
        // Criar Employee válido
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

        // Persiste o funcionário no banco de dados para ser usado nos testes
        employee = employeeService.createEmployee(employee, 1L);
        assertNotNull(employee.getId());
    }

    @Test
    @DisplayName("Lista folhas vazia quando sistema sem cálculos")
    // Valida que a lista de folhas está vazia antes de qualquer cálculo
    void deveListarFolhasVazioInicialmente() {
        ResponseEntity<?> response = controller.payrollList();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((List<?>) response.getBody()).isEmpty());
    }

    @Test
    @DisplayName("Erro ao calcular com employeeId inválido")
    // Valida mensagem de erro e status 500 ao enviar employeeId inválido
    void deveFalharCalculoFolhaComEmployeeIdInvalido() {
        PayrollCalculationRequestDTO requestDTO = new PayrollCalculationRequestDTO();
        // Usamos um ID que sabemos que não existe, o que deve levar a um erro no Service
        requestDTO.setEmployeeId(-1L); 
        requestDTO.setReferenceMonth("2025-10");

        // Chama o método com o DTO
        ResponseEntity<?> response = controller.calculatePayroll(requestDTO, null);
        
        // Verifica o status de erro e a mensagem
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Erro ao calcular folha"));
    }

    @Test
    @DisplayName("Folha inexistente retorna 404")
    // Valida retorno 404 ao consultar folha de pagamento inexistente
    void deveRetornar404AoVisualizarFolhaInexistente() {
        ResponseEntity<?> response = controller.viewPayroll(999L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Folha de pagamento não encontrada", response.getBody());
    }

    @Test
    @DisplayName("Folhas por funcionário inexistente retornam 404")
    // Valida retorno 404 ao listar folhas para funcionário inexistente
    void deveRetornar404AoListarFolhasDeFuncionarioInexistente() {
        ResponseEntity<?> response = controller.viewEmployeePayrolls(999L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Funcionário não encontrado", response.getBody());
    }
}