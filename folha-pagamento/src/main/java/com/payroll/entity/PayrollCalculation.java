package com.payroll.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payroll_calculations")
public class PayrollCalculation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "reference_month")
    private String referenceMonth;

    @Column(name = "gross_salary")
    private BigDecimal grossSalary;

    @Column(name = "net_salary")
    private BigDecimal netSalary;

    @Column(name = "inss_discount")
    private BigDecimal inssDiscount;

    @Column(name = "irpf_discount")
    private BigDecimal irpfDiscount;

    @Column(name = "transport_discount")
    private BigDecimal transportDiscount;

    @Column(name = "fgts_value")
    private BigDecimal fgtsValue;

    @Column(name = "dangerous_bonus")
    private BigDecimal dangerousBonus;

    @Column(name = "unhealthy_bonus")
    private BigDecimal unhealthyBonus;

    @Column(name = "meal_voucher_value")
    private BigDecimal mealVoucherValue;

    @Column(name = "hourly_wage")
    private BigDecimal hourlyWage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private Long createdBy;

    // Constructors
    public PayrollCalculation() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
}