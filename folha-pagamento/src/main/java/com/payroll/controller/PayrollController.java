package com.payroll.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

import com.payroll.dtos.employee.EmployeeSummaryDTO; // Importa DTO auxiliar
import com.payroll.dtos.payroll.PayrollCalculationRequestDTO; // NOVO: DTO de Entrada
import com.payroll.dtos.payroll.PayrollCalculationResponseDTO; // NOVO: DTO de Saída
import com.payroll.entity.Employee;
import com.payroll.entity.PayrollCalculation;
import com.payroll.entity.User;
import com.payroll.service.EmployeeService;
import com.payroll.service.PayrollService;
import com.payroll.service.UserService;
import jakarta.validation.Valid; // NOVO: Para validação

@RestController
@RequestMapping("/api/payroll")
public class PayrollController implements IPayrollController {

    @Autowired
    private PayrollService payrollService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    // --- Mappers Manuais (Copie estes métodos ou use o MapStruct) ---

    // Converte Entidade para DTO de Resposta
    private PayrollCalculationResponseDTO toResponseDTO(PayrollCalculation calculation) {
        if (calculation == null) return null;
        
        PayrollCalculationResponseDTO dto = new PayrollCalculationResponseDTO();
        dto.setId(calculation.getId());
        dto.setReferenceMonth(calculation.getReferenceMonth());
        dto.setGrossSalary(calculation.getGrossSalary());
        dto.setNetSalary(calculation.getNetSalary());
        // ... setar todos os campos de cálculo ...
        dto.setCreatedAt(calculation.getCreatedAt());
        dto.setCreatedBy(calculation.getCreatedBy());
        
        // Mapeia o Employee para o DTO Resumo (SERIALIZAÇÃO SEGURA)
        if (calculation.getEmployee() != null) {
            Employee emp = calculation.getEmployee();
            EmployeeSummaryDTO summary = new EmployeeSummaryDTO(emp.getId(), emp.getFullName(), emp.getPosition());
            dto.setEmployee(summary);
        }
        return dto;
    }

    // Mapeamento Employee para Summary DTO (Auxiliar)
    private EmployeeSummaryDTO toSummaryDTO(Employee employee) {
        if (employee == null) return null;
        return new EmployeeSummaryDTO(employee.getId(), employee.getFullName(), employee.getPosition());
    }

    // -----------------------------------------------------------------

    // Listar todas as folhas de pagamento
    @GetMapping
    @Override
    public ResponseEntity<List<PayrollCalculationResponseDTO>> payrollList() {
        List<PayrollCalculation> calculations = payrollService.getAllPayrolls();
        
        // SERIALIZAÇÃO: Converte List<PayrollCalculation> para List<PayrollCalculationResponseDTO>
        List<PayrollCalculationResponseDTO> dtos = calculations.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    // Calcular folha de pagamento
    @PostMapping("/calculate")
    @Override
    // DESSERIALIZAÇÃO: Recebe DTO de Requisição e aplica validação
    public ResponseEntity<?> calculatePayroll(@RequestBody @Valid PayrollCalculationRequestDTO requestDTO,
                                              @AuthenticationPrincipal UserDetails currentUser) {
        try {
            // ACESSANDO DADOS DO DTO:
            Long employeeId = requestDTO.getEmployeeId();
            String referenceMonth = requestDTO.getReferenceMonth();

            // Obter ID do usuário logado
            User user = userService.findByUsername(currentUser.getUsername()).orElse(null);
            Long userId = user != null ? user.getId() : null;

            PayrollCalculation calculation = payrollService.calculatePayroll(employeeId, referenceMonth, userId);

            // SERIALIZAÇÃO: Converte a Entidade salva para DTO de Resposta
            PayrollCalculationResponseDTO responseDTO = toResponseDTO(calculation);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao calcular folha: " + e.getMessage());
        }
    }

    // Visualizar folha de pagamento por ID
    @GetMapping("/{id}")
    @Override
    public ResponseEntity<?> viewPayroll(@PathVariable Long id) {
        // Nota: Idealmente, o Service deve ter um método getPayrollById(id)
        Optional<PayrollCalculation> calculationOpt = payrollService.getAllPayrolls().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

        if (calculationOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Folha de pagamento não encontrada");
        }
        
        // SERIALIZAÇÃO: Converte Entidade para DTO de Resposta
        PayrollCalculationResponseDTO responseDTO = toResponseDTO(calculationOpt.get());

        return ResponseEntity.ok(responseDTO);
    }

    // Visualizar folhas de pagamento de um funcionário específico
    @GetMapping("/employee/{employeeId}")
    @Override
    public ResponseEntity<?> viewEmployeePayrolls(@PathVariable Long employeeId) {
        Optional<Employee> employeeOpt = employeeService.getEmployeeById(employeeId);
        if (employeeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Funcionário não encontrado");
        }
        
        // SERIALIZAÇÃO DE OBJETO ANINHADO: Converte Employee para DTO Resumo
        EmployeeSummaryDTO employeeDTO = toSummaryDTO(employeeOpt.get());

        List<PayrollCalculation> calculations = payrollService.getEmployeePayrolls(employeeId);
        
        // SERIALIZAÇÃO DE LISTA: Converte List<PayrollCalculation> para List<PayrollCalculationResponseDTO>
        List<PayrollCalculationResponseDTO> calculationDTOs = calculations.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        
        // Retorna o Map contendo os DTOs
        return ResponseEntity.ok(Map.of(
                "employee", employeeDTO, // DTO em vez da Entidade Employee
                "calculations", calculationDTOs // Lista de DTOs em vez de Entidades
        ));
    }
}