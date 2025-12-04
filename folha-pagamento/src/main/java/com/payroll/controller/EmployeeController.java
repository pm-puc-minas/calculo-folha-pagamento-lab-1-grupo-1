package com.payroll.controller;

import com.payroll.dto.EmployeeDTO;
import com.payroll.entity.Employee;
import com.payroll.entity.User;
import com.payroll.service.EmployeeService;
import com.payroll.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "http://localhost:5173") // libera chamadas do front local
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    // Listar todos os funcionarios
    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> listEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        List<EmployeeDTO> dtos = employees.stream().map(EmployeeDTO::fromEntity).toList();
        return ResponseEntity.ok(dtos);
    }

    // Criar um novo funcionario (payload alinhado ao front)
    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody EmployeeDTO employeeDto,
                                            @AuthenticationPrincipal UserDetails currentUser) {
        if (employeeService.existsByCpf(employeeDto.cpf)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("CPF ja cadastrado");
        }

        Long userId = null;
        if (currentUser != null) {
            User user = userService.findByUsername(currentUser.getUsername()).orElse(null);
            userId = user != null ? user.getId() : null;
        }

        Employee payload = toEntity(employeeDto); // converte DTO usado pelo front para a entidade
        Employee saved = employeeService.createEmployee(payload, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(EmployeeDTO.fromEntity(saved));
    }

    // Visualizar funcionario por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> viewEmployee(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        if (employee.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Funcionario nao encontrado");
        }
        return ResponseEntity.ok(EmployeeDTO.fromEntity(employee.get()));
    }

    // Atualizar funcionario (payload alinhado ao front)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id,
                                            @RequestBody EmployeeDTO employeeDto) {
        try {
            Employee payload = toEntity(employeeDto); // reaproveita mesmo mapeamento de entrada
            Employee updatedEmployee = employeeService.updateEmployee(id, payload);
            return ResponseEntity.ok(EmployeeDTO.fromEntity(updatedEmployee));

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Funcionario nao encontrado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar funcionario: " + e.getMessage());
        }
    }

    // Deletar funcionario
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        try {
            employeeService.deleteEmployee(id);
            return ResponseEntity.ok("Deletado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao excluir funcionario: " + e.getMessage());
        }
    }

    private Employee toEntity(EmployeeDTO dto) {
        Employee e = new Employee();
        e.setFullName(dto.name);
        e.setCpf(dto.cpf);
        e.setRg(dto.cpf != null ? dto.cpf : "N/A"); // fallback simples
        LocalDate dataAdmissao = (dto.admissionDate != null && !dto.admissionDate.isBlank())
                ? LocalDate.parse(dto.admissionDate)
                : LocalDate.now();
        e.setAdmissionDate(dataAdmissao);
        e.setPosition(dto.position);
        e.setSalary(dto.baseSalary != null ? dto.baseSalary : BigDecimal.ZERO);
        e.setDependents(dto.dependents != null ? dto.dependents : 0);
        e.setWeeklyHours(dto.weeklyHours != null ? dto.weeklyHours : 40);
        boolean hazard = dto.hasHazardPay != null && dto.hasHazardPay;
        e.setDangerousWork(hazard);
        e.setDangerousPercentage(hazard ? new BigDecimal("0.30") : BigDecimal.ZERO);
        String insa = dto.insalubrity != null ? dto.insalubrity : "NONE";
        e.setUnhealthyLevel(insa);
        e.setUnhealthyWork(!"NONE".equalsIgnoreCase(insa));
        if (dto.mealVoucherDaily != null) {
            e.setMealVoucherValue(dto.mealVoucherDaily);
            e.setMealVoucher(dto.mealVoucherDaily.compareTo(BigDecimal.ZERO) > 0);
        }
        if (dto.transportVoucherValue != null) {
            e.setTransportVoucher(dto.transportVoucherValue.compareTo(BigDecimal.ZERO) > 0);
        }
        return e;
    }
}
