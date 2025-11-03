package com.payroll.controller;

import com.payroll.dtos.employee.EmployeeRequestDTO; 
import com.payroll.dtos.employee.EmployeeResponseDTO; 
import com.payroll.entity.Employee;
import com.payroll.entity.User;
import com.payroll.service.EmployeeService;
import com.payroll.service.UserService;
import jakarta.validation.Valid; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController implements IEmployeeController { 

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    

    
    private EmployeeResponseDTO toResponseDTO(Employee employee) {
        if (employee == null) return null;
        
        
        
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(employee.getId());
        dto.setFullName(employee.getFullName());
        dto.setCpf(employee.getCpf()); 
        dto.setPosition(employee.getPosition());
        dto.setSalary(employee.getSalary());
        dto.setAdmissionDate(employee.getAdmissionDate());
        dto.setCreatedAt(employee.getCreatedAt());
        

        return dto;
    }

    
    private Employee fromRequestDTO(EmployeeRequestDTO requestDTO) {
        
        Employee employee = new Employee();
        
        
        employee.setFullName(requestDTO.getFullName());
        employee.setCpf(requestDTO.getCpf());
        employee.setRg(requestDTO.getRg());
        employee.setPosition(requestDTO.getPosition());
        employee.setSalary(requestDTO.getSalary());
        employee.setAdmissionDate(requestDTO.getAdmissionDate());
        employee.setDependents(requestDTO.getDependents());
        employee.setWeeklyHours(requestDTO.getWeeklyHours());
        

        return employee;
    }

    

    
    @GetMapping
    @Override
    public ResponseEntity<List<EmployeeResponseDTO>> listEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        
        
        List<EmployeeResponseDTO> dtos = employees.stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(dtos);
    }

    
    @PostMapping
    @Override
    
    public ResponseEntity<?> createEmployee(@RequestBody @Valid EmployeeRequestDTO requestDTO,
                                            @AuthenticationPrincipal UserDetails currentUser) {
        
        

        
        if (employeeService.existsByCpf(requestDTO.getCpf())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("CPF já cadastrado");
        }
        
        
        Employee employeeToSave = fromRequestDTO(requestDTO);

        
        Long userId = null;
        if (currentUser != null) {
            User user = userService.findByUsername(currentUser.getUsername()).orElse(null);
            userId = user != null ? user.getId() : null;
        }

        Employee saved = employeeService.createEmployee(employeeToSave, userId);
        
        
        EmployeeResponseDTO responseDTO = toResponseDTO(saved);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }


    
    @GetMapping("/{id}")
    @Override
    public ResponseEntity<?> viewEmployee(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        
        if (employee.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Funcionário não encontrado");
        }
        
        
        EmployeeResponseDTO responseDTO = toResponseDTO(employee.get());
        
        return ResponseEntity.ok(responseDTO);
    }

    
    @PutMapping("/{id}")
    @Override
    
    public ResponseEntity<?> updateEmployee(@PathVariable Long id,
                                            @RequestBody @Valid EmployeeRequestDTO requestDTO) {
        try {
            
            Employee employeeData = fromRequestDTO(requestDTO);
            
            Employee updatedEmployee = employeeService.updateEmployee(id, employeeData);

            
            EmployeeResponseDTO responseDTO = toResponseDTO(updatedEmployee);

            
            return ResponseEntity.ok(responseDTO);

        } catch (NoSuchElementException e) {
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Funcionário não encontrado");
        } catch (Exception e) {
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar funcionário: " + e.getMessage());
        }
    }


    
    @DeleteMapping("/{id}")
    @Override
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