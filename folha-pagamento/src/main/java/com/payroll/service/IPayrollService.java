package com.payroll.service;

import com.payroll.entity.Employee;
import com.payroll.entity.PayrollCalculation;
import com.payroll.model.Employee.GrauInsalubridade;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interface para serviço de folha de pagamento com métodos de cálculo específicos
 */
public interface IPayrollService {
    
    /**
     * Calcula folha de pagamento completa
     * @param employeeId ID do funcionário
     * @param referenceMonth Mês de referência
     * @param calculatedBy ID do usuário que calculou
     * @return Cálculo realizado
     */
    PayrollCalculation calculatePayroll(Long employeeId, String referenceMonth, Long calculatedBy);
    
    /**
     * Calcula salário por hora
     * @param salarioBruto Salário bruto mensal
     * @param horasSemanais Horas trabalhadas por semana
     * @return Salário por hora
     */
    BigDecimal calcularSalarioHora(BigDecimal salarioBruto, int horasSemanais);
    
    /**
     * Calcula adicional de periculosidade (30% do salário)
     * @param salarioBase Salário base
     * @return Valor do adicional
     */
    BigDecimal calcularAdicionalPericulosidade(BigDecimal salarioBase);
    
    /**
     * Calcula adicional de insalubridade
     * @param salarioMinimo Salário mínimo vigente
     * @param grau Grau de insalubridade
     * @return Valor do adicional
     */
    BigDecimal calcularAdicionalInsalubridade(BigDecimal salarioMinimo, GrauInsalubridade grau);
    
    /**
     * Calcula desconto do vale transporte (máximo 6% do salário)
     * @param salarioBruto Salário bruto
     * @param valorEntregue Valor do vale transporte entregue
     * @return Valor do desconto
     */
    BigDecimal calcularDescontoValeTransporte(BigDecimal salarioBruto, BigDecimal valorEntregue);
    
    /**
     * Calcula valor do vale alimentação
     * @param valorDiario Valor diário do vale
     * @param diasTrabalhados Dias trabalhados no mês
     * @return Valor total do vale alimentação
     */
    BigDecimal calcularValeAlimentacao(BigDecimal valorDiario, int diasTrabalhados);
    
    /**
     * Calcula FGTS (8% do salário)
     * @param baseCalculoFGTS Base de cálculo
     * @return Valor do FGTS
     */
    BigDecimal calcularFGTS(BigDecimal baseCalculoFGTS);
    
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