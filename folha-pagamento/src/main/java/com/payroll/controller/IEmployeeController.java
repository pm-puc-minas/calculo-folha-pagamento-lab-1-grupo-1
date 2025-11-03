package com.payroll.controller;

import com.payroll.dtos.employee.EmployeeRequestDTO; // NOVO
import com.payroll.dtos.employee.EmployeeResponseDTO; // NOVO
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * Interface para operações de funcionários
 */
public interface IEmployeeController {
    
    /**
     * Lista todos os funcionários
     * @return ResponseEntity com lista de funcionários (DTOs de Resposta)
     */
    // 
    ResponseEntity<List<EmployeeResponseDTO>> listEmployees();
    
    /**
     * Cria um novo funcionário
     * @param request Dados do funcionário (DTO de Requisição)
     * @param currentUser Usuário autenticado
     * @return ResponseEntity com funcionário criado (DTO de Resposta)
     */
    // 
    ResponseEntity<?> createEmployee(EmployeeRequestDTO request, UserDetails currentUser);
    
    /**
     * Visualiza funcionário por ID
     * @param id ID do funcionário
     * @return ResponseEntity com dados do funcionário (DTO de Resposta)
     */
    // 
    ResponseEntity<?> viewEmployee(Long id);
    
    /**
     * Atualiza funcionário
     * @param id ID do funcionário
     * @param request Dados atualizados (DTO de Requisição)
     * @return ResponseEntity com funcionário atualizado (DTO de Resposta)
     */
    // 
    ResponseEntity<?> updateEmployee(Long id, EmployeeRequestDTO request);
    
    /**
     * Deleta funcionário
     * @param id ID do funcionário
     * @return ResponseEntity com mensagem de confirmação
     */
    ResponseEntity<?> deleteEmployee(Long id);
}