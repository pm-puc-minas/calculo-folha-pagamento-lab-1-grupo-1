package com.payroll.entity;

/*
 * Entidade de persistência para o Cálculo da Folha (Holerite).
 * Armazena o resultado processado (snapshot) dos pagamentos de um mês específico,
 * consolidando proventos, descontos e totais para histórico e consulta.
 */

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payroll_calculations")
public class PayrollCalculation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Dados de Vinculação ---
    
    // Funcionário ao qual este cálculo se refere
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    @NotNull
    private Employee employee;

    // Competência do cálculo (Formato: YYYY-MM)
    @Column(name = "reference_month", nullable = false)
    @NotBlank
    private String referenceMonth;

    // --- Totais Financeiros ---
    
    @Column(name = "gross_salary", nullable = false)
    @NotNull
    private BigDecimal grossSalary; // Bruto (Soma de todos os proventos)

    @Column(name = "net_salary", nullable = false)
    @NotNull
    private BigDecimal netSalary;   // Líquido (A receber após descontos)

    // --- Descontos Legais (Obrigatórios) ---
    
    @Column(name = "inss_discount", nullable = false)
    @NotNull
    private BigDecimal inssDiscount;

    @Column(name = "irpf_discount", nullable = false)
    @NotNull
    private BigDecimal irpfDiscount;

    @Column(name = "transport_discount", nullable = false)
    @NotNull
    private BigDecimal transportDiscount;

    // --- Informativos e Bases de Cálculo ---
    
    // Valor recolhido de FGTS (não é descontado do funcionário)
    @Column(name = "fgts_value", nullable = false)
    @NotNull
    private BigDecimal fgtsValue;

    @Column(name = "hourly_wage", nullable = false)
    @NotNull
    private BigDecimal hourlyWage;

    // --- Adicionais e Proventos ---
    
    @Column(name = "dangerous_bonus", nullable = false)
    @NotNull
    private BigDecimal dangerousBonus;

    @Column(name = "unhealthy_bonus", nullable = false)
    @NotNull
    private BigDecimal unhealthyBonus;
    
    @Column(name = "overtime_value")
    private BigDecimal overtimeValue;

    // --- Benefícios e Convênios (Descontos/Coparticipação) ---

    @Column(name = "meal_voucher_value", nullable = false)
    @NotNull
    private BigDecimal mealVoucherValue;

    @Column(name = "health_plan_discount")
    private BigDecimal healthPlanDiscount;

    @Column(name = "dental_plan_discount")
    private BigDecimal dentalPlanDiscount;

    @Column(name = "gym_discount")
    private BigDecimal gymDiscount;

    // --- Auditoria ---
    
    @Column(name = "created_at", nullable = false)
    @NotNull
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false)
    @NotNull
    private Long createdBy;

    // Construtor padrão com timestamp automático
    public PayrollCalculation() {
        this.createdAt = LocalDateTime.now();
    }

    // --- Getters e Setters ---
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

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

    public BigDecimal getHealthPlanDiscount() { return healthPlanDiscount; }
    public void setHealthPlanDiscount(BigDecimal healthPlanDiscount) { this.healthPlanDiscount = healthPlanDiscount; }

    public BigDecimal getDentalPlanDiscount() { return dentalPlanDiscount; }
    public void setDentalPlanDiscount(BigDecimal dentalPlanDiscount) { this.dentalPlanDiscount = dentalPlanDiscount; }

    public BigDecimal getGymDiscount() { return gymDiscount; }
    public void setGymDiscount(BigDecimal gymDiscount) { this.gymDiscount = gymDiscount; }

    public BigDecimal getOvertimeValue() { return overtimeValue; }
    public void setOvertimeValue(BigDecimal overtimeValue) { this.overtimeValue = overtimeValue; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
}