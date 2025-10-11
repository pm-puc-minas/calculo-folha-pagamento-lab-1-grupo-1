package com.payroll.controller;

import com.payroll.entity.PayrollCalculation;
import com.payroll.entity.Employee;
import com.payroll.entity.User;
import com.payroll.service.EmployeeService;
import com.payroll.service.PayrollService;
import com.payroll.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public ResponseEntity<List<PayrollCalculation>> payrollList() {
        List<PayrollCalculation> calculations = payrollService.getAllPayrolls();
        return ResponseEntity.ok(calculations);
    }

    // Calcular folha de pagamento
    @PostMapping("/calculate")
    public ResponseEntity<?> calculatePayroll(@RequestBody Map<String, String> request,
                                              @AuthenticationPrincipal UserDetails currentUser) {
        try {
            Long employeeId = Long.parseLong(request.get("employeeId"));
            String referenceMonth = request.get("referenceMonth");

            // Obter ID do usuário logado
            User user = userService.findByUsername(currentUser.getUsername()).orElse(null);
            Long userId = user != null ? user.getId() : null;

            PayrollCalculation calculation = payrollService.calculatePayroll(employeeId, referenceMonth, userId);

            return ResponseEntity.status(HttpStatus.CREATED).body(calculation);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao calcular folha: " + e.getMessage());
        }
    }

    // Visualizar folha de pagamento por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> viewPayroll(@PathVariable Long id) {
        Optional<PayrollCalculation> calculation = payrollService.getAllPayrolls().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

        if (calculation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Folha de pagamento não encontrada");
        }

        return ResponseEntity.ok(calculation.get());
    }

    // Visualizar folhas de pagamento de um funcionário específico
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> viewEmployeePayrolls(@PathVariable Long employeeId) {
        Optional<Employee> employee = employeeService.getEmployeeById(employeeId);
        if (employee.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Funcionário não encontrado");
        }

        List<PayrollCalculation> calculations = payrollService.getEmployeePayrolls(employeeId);
        return ResponseEntity.ok(Map.of(
                "employee", employee.get(),
                "calculations", calculations
        ));
    }
}
