package com.payroll.controller;

import com.payroll.dtos.payroll.PayrollCalculationRequestDTO; 
import com.payroll.dtos.payroll.PayrollCalculationResponseDTO; 
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;

/**
 * Interface para operações de folha de pagamento
 */
public interface IPayrollController {
    
    /**
     * Lista todas as folhas de pagamento
     * @return ResponseEntity com lista de cálculos (DTOs de Resposta)
     */
    // 
    ResponseEntity<List<PayrollCalculationResponseDTO>> payrollList();
    
    /**
     * Calcula folha de pagamento
     * @param request Dados para cálculo (DTO de Requisição)
     * @param currentUser Usuário autenticado
     * @return ResponseEntity com cálculo realizado (DTO de Resposta)
     */
    // 
    ResponseEntity<?> calculatePayroll(PayrollCalculationRequestDTO request, UserDetails currentUser);
    
    /**
     * Visualiza folha de pagamento por ID
     * @param id ID do cálculo
     * @return ResponseEntity com dados do cálculo (DTO de Resposta)
     */
    // 
    ResponseEntity<?> viewPayroll(Long id);
    
    /**
     * Visualiza folhas de pagamento de um funcionário
     * @param employeeId ID do funcionário
     * @return ResponseEntity com cálculos do funcionário (Map com DTOs e lista de DTOs)
     */
    // A assinatura permanece a mesma, mas o retorno será um Map de DTOs
    ResponseEntity<?> viewEmployeePayrolls(Long employeeId);
}