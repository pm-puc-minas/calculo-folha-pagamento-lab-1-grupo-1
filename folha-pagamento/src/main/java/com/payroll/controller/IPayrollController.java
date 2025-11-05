package com.payroll.controller;

import com.payroll.entity.PayrollCalculation;
import com.payroll.dto.PayrollDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;
import java.util.Map;

/**
 * Interface para operações de folha de pagamento
 */
public interface IPayrollController {
    
    /**
     * Lista todas as folhas de pagamento
     * @return ResponseEntity com lista de cálculos
     */
    ResponseEntity<List<PayrollDTO>> payrollList();
    
    /**
     * Calcula folha de pagamento
     * @param request Dados para cálculo
     * @param currentUser Usuário autenticado
     * @return ResponseEntity com cálculo realizado
     */
    ResponseEntity<?> calculatePayroll(Map<String, String> request, UserDetails currentUser);
    
    /**
     * Visualiza folha de pagamento por ID
     * @param id ID do cálculo
     * @return ResponseEntity com dados do cálculo
     */
    ResponseEntity<?> viewPayroll(Long id);
    
    /**
     * Visualiza folhas de pagamento de um funcionário
     * @param employeeId ID do funcionário
     * @return ResponseEntity com cálculos do funcionário
     */
    ResponseEntity<?> viewEmployeePayrolls(Long employeeId);
}