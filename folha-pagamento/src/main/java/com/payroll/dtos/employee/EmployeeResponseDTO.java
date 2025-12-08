package com.payroll.dtos.employee;

/*
 * Objeto de Transferência de Dados (DTO) para resposta de dados de Funcionários.
 * Estrutura os dados detalhados do funcionário a serem retornados pela API,
 * incluindo metadados de auditoria e formatação específica para o cliente (frontend).
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmployeeResponseDTO {

    private Long id;

    // --- Dados de Identificação ---
    private String fullName;
    private String cpf; 
    private String rg;  

    // --- Dados Contratuais ---
    private String position;
    
    // Formatação de data padrão ISO para consistência no frontend
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate admissionDate;

    private BigDecimal salary;
    private Integer dependents;
    private Integer weeklyHours;

    // --- Benefícios e Adicionais ---
    private Boolean transportVoucher;
    
    private Boolean mealVoucher;
    private BigDecimal mealVoucherValue;
    
    private Boolean dangerousWork;
    private BigDecimal dangerousPercentage;
    
    private Boolean unhealthyWork;
    private String unhealthyLevel;

    // --- Metadados de Auditoria ---
    // Registra quando e por quem o registro foi criado no sistema
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    private Long createdBy;
    
    public EmployeeResponseDTO() {}

    // Construtor completo para mapeamento direto de entidades para DTO
    public EmployeeResponseDTO(Long id, String fullName, String cpf, String rg, String position, LocalDate admissionDate, BigDecimal salary, Integer dependents, Integer weeklyHours, Boolean transportVoucher, Boolean mealVoucher, BigDecimal mealVoucherValue, Boolean dangerousWork, BigDecimal dangerousPercentage, Boolean unhealthyWork, String unhealthyLevel, LocalDateTime createdAt, Long createdBy) {
        this.id = id;
        this.fullName = fullName;
        this.cpf = cpf;
        this.rg = rg;
        this.position = position;
        this.admissionDate = admissionDate;
        this.salary = salary;
        this.dependents = dependents;
        this.weeklyHours = weeklyHours;
        this.transportVoucher = transportVoucher;
        this.mealVoucher = mealVoucher;
        this.mealVoucherValue = mealVoucherValue;
        this.dangerousWork = dangerousWork;
        this.dangerousPercentage = dangerousPercentage;
        this.unhealthyWork = unhealthyWork;
        this.unhealthyLevel = unhealthyLevel;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }

    // --- Getters e Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    
    public String getRg() { return rg; }
    public void setRg(String rg) { this.rg = rg; }
    
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    
    public LocalDate getAdmissionDate() { return admissionDate; }
    public void setAdmissionDate(LocalDate admissionDate) { this.admissionDate = admissionDate; }
    
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    
    public Integer getDependents() { return dependents; }
    public void setDependents(Integer dependents) { this.dependents = dependents; }
    
    public Integer getWeeklyHours() { return weeklyHours; }
    public void setWeeklyHours(Integer weeklyHours) { this.weeklyHours = weeklyHours; }
    
    public Boolean getTransportVoucher() { return transportVoucher; }
    public void setTransportVoucher(Boolean transportVoucher) { this.transportVoucher = transportVoucher; }
    
    public Boolean getMealVoucher() { return mealVoucher; }
    public void setMealVoucher(Boolean mealVoucher) { this.mealVoucher = mealVoucher; }
    
    public BigDecimal getMealVoucherValue() { return mealVoucherValue; }
    public void setMealVoucherValue(BigDecimal mealVoucherValue) { this.mealVoucherValue = mealVoucherValue; }
    
    public Boolean getDangerousWork() { return dangerousWork; }
    public void setDangerousWork(Boolean dangerousWork) { this.dangerousWork = dangerousWork; }
    
    public BigDecimal getDangerousPercentage() { return dangerousPercentage; }
    public void setDangerousPercentage(BigDecimal dangerousPercentage) { this.dangerousPercentage = dangerousPercentage; }
    
    public Boolean getUnhealthyWork() { return unhealthyWork; }
    public void setUnhealthyWork(Boolean unhealthyWork) { this.unhealthyWork = unhealthyWork; }
    
    public String getUnhealthyLevel() { return unhealthyLevel; }
    public void setUnhealthyLevel(String unhealthyLevel) { this.unhealthyLevel = unhealthyLevel; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
}