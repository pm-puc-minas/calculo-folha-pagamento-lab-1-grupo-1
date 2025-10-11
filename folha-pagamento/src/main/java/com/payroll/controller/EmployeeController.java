package com.payroll.controller;

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

    // Listar todos os funcionários
    @GetMapping
    public ResponseEntity<List<Employee>> listEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    // Criar um novo funcionário
   @PostMapping
public ResponseEntity<?> createEmployee(@RequestBody Employee employee,
                                        @AuthenticationPrincipal UserDetails currentUser) {
    if (employeeService.existsByCpf(employee.getCpf())) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("CPF já cadastrado");
    }

    // Obter ID do usuário logado, mas permitir teste sem user
    Long userId = null;
    if (currentUser != null) {
        User user = userService.findByUsername(currentUser.getUsername()).orElse(null);
        userId = user != null ? user.getId() : null;
    }

    Employee saved = employeeService.createEmployee(employee, userId);
    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
}


    // Visualizar funcionário por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> viewEmployee(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        if (employee.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Funcionário não encontrado");
        }
        return ResponseEntity.ok(employee.get());
    }

    // Atualizar funcionário
    @PutMapping("/{id}")
public ResponseEntity<?> updateEmployee(@PathVariable Long id,
                                        @RequestBody Employee employee) {
    try {
        Employee updatedEmployee = employeeService.updateEmployee(id, employee);

        // Retorna 200 OK com o objeto atualizado
        return ResponseEntity.ok(updatedEmployee);

    } catch (NoSuchElementException e) {
        // Se não existir retorna 404
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Funcionário não encontrado");
    } catch (Exception e) {
        // Para outros errosretorna 500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao atualizar funcionário: " + e.getMessage());
    }
}


    // Deletar funcionário
   @DeleteMapping("/{id}")
public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
    try {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok("Deletado com sucesso");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao excluir funcionário: " + e.getMessage());
    }
}

}
