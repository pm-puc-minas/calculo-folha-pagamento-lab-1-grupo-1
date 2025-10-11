package com.payroll.service;

import com.payroll.entity.PayrollCalculation;
import java.util.List;

/**
 * Interface para serviço de folha de pagamento
 */
public interface IPayrollService {
    
    /**
     * Calcula folha de pagamento
     * @param employeeId ID do funcionario
     * @param referenceMonth mes de referência
     * @param calculatedBy ID do usuário que calculou
     * @return Cálculo realizado
     */
    PayrollCalculation calculatePayroll(Long employeeId, String referenceMonth, Long calculatedBy);
    
    /**
     * Busca folhas de pagamento de um funcionário
     * @param employeeId ID do funcionário
     * @return Lista de cálculos
     */
    List<PayrollCalculation> getEmployeePayrolls(Long employeeId);
    
    /**
     * Lista todas as folhas de pagamento
     * @return Lista de cálculos
     */
    List<PayrollCalculation> getAllPayrolls();
}