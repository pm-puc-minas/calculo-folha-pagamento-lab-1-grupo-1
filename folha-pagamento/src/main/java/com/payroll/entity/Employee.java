package com.payroll.entity;

import com.fasterxml.jackson.annotation.JsonFormat; 
import com.fasterxml.jackson.annotation.JsonIgnore; 
import com.fasterxml.jackson.annotation.JsonManagedReference; 
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List; 

@Entity(name = "EmployeeEntity")
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "full_name")
    private String fullName;
    
    @NotBlank
    @Column(unique = true)
    private String cpf;

    
    @NotBlank
    private String rg;

    @NotBlank
    private String position;

    @NotNull
    @Column(name = "admission_date")
    @JsonFormat(pattern = "yyyy-MM-dd") 
    private LocalDate admissionDate;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal salary;
    
    @Column(name = "dependents")
    @Min(0) 
    private Integer dependents = 0;

    @NotNull
    @Min(1)
    @Column(name = "weekly_hours")
    private Integer weeklyHours;

    @Column(name = "transport_voucher")
    private Boolean transportVoucher = false;

    @Column(name = "meal_voucher")
    private Boolean mealVoucher = false;

    @Column(name = "meal_voucher_value")
    private BigDecimal mealVoucherValue = BigDecimal.ZERO;

    @Column(name = "dangerous_work")
    private Boolean dangerousWork = false;

    @Column(name = "dangerous_percentage")
    private BigDecimal dangerousPercentage = BigDecimal.ZERO;

    @Column(name = "unhealthy_work")
    private Boolean unhealthyWork = false;

    @Column(name = "unhealthy_level")
    private String unhealthyLevel;

    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") 
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private Long createdBy;
    
    public Employee() {
        this.createdAt = LocalDateTime.now();
    }
    
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
    
    public Integer getDependents() {return dependents;}
    public void setDependents(Integer dependents) {this.dependents = dependents;}

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