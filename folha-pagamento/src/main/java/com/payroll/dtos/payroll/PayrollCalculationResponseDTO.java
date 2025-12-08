package com.payroll.dtos.payroll;

/*
 * Objeto de Transferência de Dados (DTO) para resposta do cálculo de folha.
 * Consolida todos os resultados financeiros do processamento (proventos, descontos e totais),
 * servindo como base para a visualização do holerite (contracheque) no frontend.
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import com.payroll.dtos.employee.EmployeeSummaryDTO; 
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PayrollCalculationResponseDTO {

    private Long id;

    // Contexto do cálculo (Quem e Quando)
    private EmployeeSummaryDTO employee; 
    private String referenceMonth;

    // --- Totais Principais ---
    private BigDecimal grossSalary; // Salário Bruto
    private BigDecimal netSalary;   // Salário Líquido (A receber)

    // --- Descontos Legais e Benefícios ---
    private BigDecimal inssDiscount;      // Previdência Social
    private BigDecimal irpfDiscount;      // Imposto de Renda
    private BigDecimal transportDiscount; // Vale Transporte
    
    // --- Informativos e Adicionais ---
    private BigDecimal fgtsValue;         // Fundo de Garantia (Informativo)
    private BigDecimal dangerousBonus;    // Adicional de Periculosidade
    private BigDecimal unhealthyBonus;    // Adicional de Insalubridade
    private BigDecimal mealVoucherValue;  
    private BigDecimal hourlyWage;        // Valor da hora base

    // --- Metadados de Auditoria ---
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    private Long createdBy;
    
    public PayrollCalculationResponseDTO() {}
    
    // Construtor completo para mapeamento da entidade calculada para o DTO
    public PayrollCalculationResponseDTO(Long id, EmployeeSummaryDTO employee, String referenceMonth, BigDecimal grossSalary, BigDecimal netSalary, BigDecimal inssDiscount, BigDecimal irpfDiscount, BigDecimal transportDiscount, BigDecimal fgtsValue, BigDecimal dangerousBonus, BigDecimal unhealthyBonus, BigDecimal mealVoucherValue, BigDecimal hourlyWage, LocalDateTime createdAt, Long createdBy) {
        this.id = id;
        this.employee = employee;
        this.referenceMonth = referenceMonth;
        this.grossSalary = grossSalary;
        this.netSalary = netSalary;
        this.inssDiscount = inssDiscount;
        this.irpfDiscount = irpfDiscount;
        this.transportDiscount = transportDiscount;
        this.fgtsValue = fgtsValue;
        this.dangerousBonus = dangerousBonus;
        this.unhealthyBonus = unhealthyBonus;
        this.mealVoucherValue = mealVoucherValue;
        this.hourlyWage = hourlyWage;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }    
    
    // --- Getters e Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public EmployeeSummaryDTO getEmployee() { return employee; }
    public void setEmployee(EmployeeSummaryDTO employee) { this.employee = employee; }
    
    public String getReferenceMonth() { return referenceMonth; }
    public void setReferenceMonth(String referenceMonth) { this.referenceMonth = referenceMonth; }
    
    public BigDecimal getGrossSalary() { return grossSalary; }
    public void setGrossSalary(BigDecimal grossSalary) { this.grossSalary = grossSalary; }
    
    public BigDecimal getNetSalary() { return netSalary; }
    public void setNetSalary(BigDecimal netSalary) { this.netSalary = netSalary; }
    
    public BigDecimal getInssDiscount() { return inssDiscount; }
    public void setInssDiscount(BigDecimal inssDiscount) { this.inssDiscount = inssDiscount; }
    
    public BigDecimal getIrpfDiscount() { return irpfDiscount; }
    public void setIrpfDiscount(BigDecimal irpfDiscount) { this.irpfDiscount = irpfDiscount; }
    
    public BigDecimal getTransportDiscount() { return transportDiscount; }
    public void setTransportDiscount(BigDecimal transportDiscount) { this.transportDiscount = transportDiscount; }
    
    public BigDecimal getFgtsValue() { return fgtsValue; }
    public void setFgtsValue(BigDecimal fgtsValue) { this.fgtsValue = fgtsValue; }
    
    public BigDecimal getDangerousBonus() { return dangerousBonus; }
    public void setDangerousBonus(BigDecimal dangerousBonus) { this.dangerousBonus = dangerousBonus; }
    
    public BigDecimal getUnhealthyBonus() { return unhealthyBonus; }
    public void setUnhealthyBonus(BigDecimal unhealthyBonus) { this.unhealthyBonus = unhealthyBonus; }
    
    public BigDecimal getMealVoucherValue() { return mealVoucherValue; }
    public void setMealVoucherValue(BigDecimal mealVoucherValue) { this.mealVoucherValue = mealVoucherValue; }
    
    public BigDecimal getHourlyWage() { return hourlyWage; }
    public void setHourlyWage(BigDecimal hourlyWage) { this.hourlyWage = hourlyWage; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
}