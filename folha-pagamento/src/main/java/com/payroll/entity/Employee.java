package com.payroll.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
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
    private LocalDate admissionDate;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal salary;
    @Column(name = "dependents")
private Integer dependents = 0; 

    @NotNull
    @Min(1)
    @Column(name = "weekly_hours")
    private Integer weeklyHours;

    @Column(name = "transport_voucher")
    private Boolean transportVoucher = false;

    @Column(name = "transport_voucher_value")
    private BigDecimal transportVoucherValue = BigDecimal.ZERO;

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

    @Column(name = "health_plan")
    private Boolean healthPlan = false;

    @Column(name = "health_plan_value")
    private BigDecimal healthPlanValue = BigDecimal.ZERO;

    @Column(name = "dental_plan")
    private Boolean dentalPlan = false;

    @Column(name = "dental_plan_value")
    private BigDecimal dentalPlanValue = BigDecimal.ZERO;

    @Column(name = "gym")
    private Boolean gym = false;

    @Column(name = "gym_value")
    private BigDecimal gymValue = BigDecimal.ZERO;

    @Column(name = "time_bank")
    private Boolean timeBank = false;

    @Column(name = "time_bank_hours")
    private BigDecimal timeBankHours = BigDecimal.ZERO;

    @Column(name = "overtime_eligible")
    private Boolean overtimeEligible = false;

    @Column(name = "overtime_hours")
    private BigDecimal overtimeHours = BigDecimal.ZERO;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private Long createdBy;

    // Constructors
    public Employee() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
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

    public Integer getWeeklyHours() { return weeklyHours; }
    public void setWeeklyHours(Integer weeklyHours) { this.weeklyHours = weeklyHours; }


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

    public Boolean getHealthPlan() { return healthPlan; }
    public void setHealthPlan(Boolean healthPlan) { this.healthPlan = healthPlan; }

    public BigDecimal getHealthPlanValue() { return healthPlanValue; }
    public void setHealthPlanValue(BigDecimal healthPlanValue) { this.healthPlanValue = healthPlanValue; }

    public Boolean getDentalPlan() { return dentalPlan; }
    public void setDentalPlan(Boolean dentalPlan) { this.dentalPlan = dentalPlan; }

    public BigDecimal getDentalPlanValue() { return dentalPlanValue; }
    public void setDentalPlanValue(BigDecimal dentalPlanValue) { this.dentalPlanValue = dentalPlanValue; }

    public Boolean getGym() { return gym; }
    public void setGym(Boolean gym) { this.gym = gym; }

    public BigDecimal getGymValue() { return gymValue; }
    public void setGymValue(BigDecimal gymValue) { this.gymValue = gymValue; }

    public Boolean getTimeBank() { return timeBank; }
    public void setTimeBank(Boolean timeBank) { this.timeBank = timeBank; }

    public BigDecimal getTimeBankHours() { return timeBankHours; }
    public void setTimeBankHours(BigDecimal timeBankHours) { this.timeBankHours = timeBankHours; }

    public Boolean getOvertimeEligible() { return overtimeEligible; }
    public void setOvertimeEligible(Boolean overtimeEligible) { this.overtimeEligible = overtimeEligible; }

    public BigDecimal getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(BigDecimal overtimeHours) { this.overtimeHours = overtimeHours; }

    public Boolean getTransportVoucher() { return transportVoucher; }
    public void setTransportVoucher(Boolean transportVoucher) { this.transportVoucher = transportVoucher; }

    public BigDecimal getTransportVoucherValue() { return transportVoucherValue; }
    public void setTransportVoucherValue(BigDecimal transportVoucherValue) { this.transportVoucherValue = transportVoucherValue; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getDependents() {return dependents;}
   public void setDependents(Integer dependents) {this.dependents = dependents;}

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
}