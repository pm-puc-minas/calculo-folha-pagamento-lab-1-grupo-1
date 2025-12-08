package com.payroll.controller;

/*
 * Controlador REST para operações de folha de pagamento.
 * Gerencia o cálculo, listagem e consulta detalhada dos pagamentos
 * dos funcionários, integrando dados de usuários e regras de cálculo.
 */

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payroll.entity.Employee;
import com.payroll.entity.PayrollCalculation;
import com.payroll.dto.PayrollDTO;
import com.payroll.entity.User;
import com.payroll.service.EmployeeService;
import com.payroll.service.PayrollService;
import com.payroll.service.UserService;

@RestController
@RequestMapping("/api/payroll")
public class PayrollController implements IPayrollController {

    @Autowired
    private PayrollService payrollService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    @GetMapping
    @Override
    public ResponseEntity<List<PayrollDTO>> payrollList() {
        // Recuperar todos os registros de folha de pagamento processados
        List<PayrollCalculation> calculations = payrollService.getAllPayrolls();
        
        // Converter entidades para DTOs antes de retornar à API
        List<PayrollDTO> dtos = calculations.stream()
                .map(pc -> PayrollDTO.fromEntity(pc, null))
                .toList();
        
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/calculate")
    @Override
    public ResponseEntity<?> calculatePayroll(@RequestBody Map<String, String> request,
                                              @AuthenticationPrincipal UserDetails currentUser) {
        try {
            // Extrair parâmetros da requisição
            Long employeeId = Long.parseLong(request.get("employeeId"));
            String referenceMonth = request.get("referenceMonth");

            // Identificar o usuário que solicitou o cálculo (para auditoria)
            Long userId = null;
            if (currentUser != null) {
                User user = userService.findByUsername(currentUser.getUsername()).orElse(null);
                userId = user != null ? user.getId() : null;
            }

            // Executar a regra de negócio do cálculo da folha
            PayrollCalculation calculation = payrollService.calculatePayroll(employeeId, referenceMonth, userId);
            
            // Retornar o resultado formatado com status 201 (Created)
            PayrollDTO dto = PayrollDTO.fromEntity(calculation, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);

        } catch (Exception e) {
            // Tratar erros de processamento e retornar mensagem amigável
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao calcular folha: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<?> viewPayroll(@PathVariable Long id) {
        // Buscar folha específica filtrando a lista total pelo ID
        Optional<PayrollCalculation> calculation = payrollService.getAllPayrolls().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

        // Verificar se o registro foi encontrado
        if (calculation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Folha de pagamento nao encontrada");
        }

        PayrollCalculation pc = calculation.get();
        return ResponseEntity.ok(PayrollDTO.fromEntity(pc, null));
    }

    @GetMapping("/employee/{employeeId}")
    @Override
    public ResponseEntity<?> viewEmployeePayrolls(@PathVariable Long employeeId) {
        // Validar a existência do funcionário antes de buscar os dados
        Optional<Employee> employee = employeeService.getEmployeeById(employeeId);
        if (employee.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Funcionario nao encontrado");
        }
        
        // Recuperar histórico de pagamentos do funcionário específico
        List<PayrollCalculation> calculations = payrollService.getEmployeePayrolls(employeeId);
        List<PayrollDTO> dtos = calculations.stream()
                .map(pc -> PayrollDTO.fromEntity(pc, null))
                .toList();
        
        return ResponseEntity.ok(dtos);
    }
}