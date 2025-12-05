package com.payroll.controller;

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

    // Listar todas as folhas de pagamento
    @GetMapping
    @Override
    public ResponseEntity<List<PayrollDTO>> payrollList() {
        List<PayrollCalculation> calculations = payrollService.getAllPayrolls();
        List<PayrollDTO> dtos = calculations.stream()
                .map(pc -> PayrollDTO.fromEntity(pc, null))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    // Calcular folha de pagamento
    @PostMapping("/calculate")
    @Override
    public ResponseEntity<?> calculatePayroll(@RequestBody Map<String, String> request,
                                              @AuthenticationPrincipal UserDetails currentUser) {
        try {
            Long employeeId = Long.parseLong(request.get("employeeId"));
            String referenceMonth = request.get("referenceMonth");

            // Obter ID do usuario logado (opcional)
            Long userId = null;
            if (currentUser != null) {
                User user = userService.findByUsername(currentUser.getUsername()).orElse(null);
                userId = user != null ? user.getId() : null;
            }

            PayrollCalculation calculation = payrollService.calculatePayroll(employeeId, referenceMonth, userId);
            PayrollDTO dto = PayrollDTO.fromEntity(calculation, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao calcular folha: " + e.getMessage());
        }
    }

    // Visualizar folha de pagamento por ID
    @GetMapping("/{id}")
    @Override
    public ResponseEntity<?> viewPayroll(@PathVariable Long id) {
        Optional<PayrollCalculation> calculation = payrollService.getAllPayrolls().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

        if (calculation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Folha de pagamento nao encontrada");
        }

        PayrollCalculation pc = calculation.get();
        return ResponseEntity.ok(PayrollDTO.fromEntity(pc, null));
    }

    // Visualizar folhas de pagamento de um funcionario especifico
    @GetMapping("/employee/{employeeId}")
    @Override
    public ResponseEntity<?> viewEmployeePayrolls(@PathVariable Long employeeId) {
        Optional<Employee> employee = employeeService.getEmployeeById(employeeId);
        if (employee.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Funcionario nao encontrado");
        }
        List<PayrollCalculation> calculations = payrollService.getEmployeePayrolls(employeeId);
        List<PayrollDTO> dtos = calculations.stream()
                .map(pc -> PayrollDTO.fromEntity(pc, null))
                .toList();
        return ResponseEntity.ok(dtos);
    }
}
