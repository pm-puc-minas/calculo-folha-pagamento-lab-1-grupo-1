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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    // Listar todos os funcionarios (com filtro opcional)
    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> listEmployees(@RequestParam(required = false) String search) {
        List<Employee> employees = employeeService.searchEmployees(search);
        List<EmployeeDTO> dtos = employees.stream().map(EmployeeDTO::fromEntity).toList();
        return ResponseEntity.ok(dtos);
    }

    // Criar um novo funcionario
    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody EmployeeDTO payload,
                                            @AuthenticationPrincipal UserDetails currentUser) {
        Employee employee = EmployeeDTO.toEntity(payload);
        if (employeeService.existsByCpf(employee.getCpf())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("CPF ja cadastrado");
        }

        Long userId = null;
        if (currentUser != null) {
            User user = userService.findByUsername(currentUser.getUsername()).orElse(null);
            userId = user != null ? user.getId() : null;
        }

        Employee saved = employeeService.createEmployee(employee, userId);
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

    // Atualizar funcionario
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id,
                                            @RequestBody EmployeeDTO payload) {
        try {
            Employee updatedEmployee = employeeService.updateEmployee(id, EmployeeDTO.toEntity(payload));
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
}
