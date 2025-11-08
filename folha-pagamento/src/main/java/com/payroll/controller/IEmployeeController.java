package com.payroll.controller;

import com.payroll.entity.Employee;
import com.payroll.dto.EmployeeDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;

/**
 * Interface para operações de funcionários
 */
public interface IEmployeeController {
    
    /**
     * Lista todos os funcionários
     * @return ResponseEntity com lista de funcionários
     */
    ResponseEntity<List<EmployeeDTO>> listEmployees();
    
    /**
     * Cria um novo funcionário
     * @param employee Dados do funcionário
     * @param currentUser Usuário autenticado
     * @return ResponseEntity com funcionário criado
     */
    ResponseEntity<?> createEmployee(Employee employee, UserDetails currentUser);
    
    /**
     * Visualiza funcionário por ID
     * @param id ID do funcionário
     * @return ResponseEntity com dados do funcionário
     */
    ResponseEntity<?> viewEmployee(Long id);
    
    /**
     * Atualiza funcionário
     * @param id ID do funcionário
     * @param employee Dados atualizados
     * @return ResponseEntity com funcionário atualizado
     */
    ResponseEntity<?> updateEmployee(Long id, Employee employee);
    
    /**
     * Deleta funcionário
     * @param id ID do funcionário
     * @return ResponseEntity com mensagem de confirmação
     */
    ResponseEntity<?> deleteEmployee(Long id);
}
