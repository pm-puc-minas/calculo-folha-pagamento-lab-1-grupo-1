package com.payroll.dtos.employee;

/*
 * Objeto de Transferência de Dados (DTO) para requisições de Funcionários.
 * Recebe e valida os dados de cadastro e atualização de funcionários vindos da API,
 * garantindo a integridade das informações antes do processamento de regras de negócio.
 */

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class EmployeeRequestDTO {
    
    private Long id;

    // --- Dados de Identificação Pessoal ---
    @NotBlank(message = "O nome completo é obrigatório.")
    private String fullName;

    @NotBlank(message = "O CPF é obrigatório.")
    private String cpf;

    @NotBlank(message = "O RG é obrigatório.")
    private String rg;

    // --- Dados Contratuais e Financeiros ---
    @NotBlank(message = "O cargo é obrigatório.")
    private String position;

    @NotNull(message = "A data de admissão é obrigatória.")
    private LocalDate admissionDate;

    @NotNull(message = "O salário é obrigatório.")
    @DecimalMin(value = "0.0", inclusive = false, message = "O salário deve ser maior que zero.")
    private BigDecimal salary;
    
    @Min(value = 0, message = "O número de dependentes não pode ser negativo.")
    private Integer dependents = 0; 

    @NotNull(message = "A carga horária semanal é obrigatória.")
    @Min(value = 1, message = "A carga horária deve ser no mínimo 1 hora.")
    private Integer weeklyHours;

    // --- Configuração de Benefícios e Adicionais ---
    private Boolean transportVoucher = false;
    
    private Boolean mealVoucher = false;
    private BigDecimal mealVoucherValue = BigDecimal.ZERO;
    
    // Adicionais de Periculosidade e Insalubridade
    private Boolean dangerousWork = false;
    private BigDecimal dangerousPercentage = BigDecimal.ZERO;
    
    private Boolean unhealthyWork = false;
    private String unhealthyLevel;

    public EmployeeRequestDTO() {}

    public EmployeeRequestDTO(Long id, String fullName, String cpf, String rg, String position, LocalDate admissionDate, BigDecimal salary, Integer dependents, Integer weeklyHours, Boolean transportVoucher, Boolean mealVoucher, BigDecimal mealVoucherValue, Boolean dangerousWork, BigDecimal dangerousPercentage, Boolean unhealthyWork, String unhealthyLevel) {
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
    }
    
    // --- Getters e Setters ---
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
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
}