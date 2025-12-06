package com.payroll.dto;

import com.payroll.entity.Employee;
import java.math.BigDecimal;
import java.time.LocalDate;

public class EmployeeDTO {
    public Long id;
    public String name;
    public String cpf;
    public String position;
    public String department;
    public String admissionDate;
    public BigDecimal baseSalary;
    public Integer dependents;
    public Boolean hasHazardPay;
    public String insalubrity; // NONE, LOW, MEDIUM, HIGH
    public BigDecimal transportVoucherValue;
    public BigDecimal mealVoucherDaily;
    public Integer workDaysMonth;
    public Integer weeklyHours;

    public Boolean hasHealthPlan;
    public BigDecimal healthPlanValue;
    public Boolean hasDentalPlan;
    public BigDecimal dentalPlanValue;
    public Boolean hasGym;
    public BigDecimal gymValue;
    public Boolean hasTimeBank;
    public BigDecimal timeBankHours;
    public Boolean hasOvertime;
    public BigDecimal overtimeHours;

    // Compatibilidade com frontend: alias grossSalary
    public BigDecimal grossSalary;

    public static EmployeeDTO fromEntity(Employee e) {
        EmployeeDTO dto = new EmployeeDTO();
        if (e == null) return dto;
        dto.id = e.getId();
        dto.name = e.getFullName();
        dto.cpf = e.getCpf();
        dto.position = e.getPosition();
        dto.department = ""; // nao ha campo dedicado
        dto.admissionDate = e.getAdmissionDate() != null ? e.getAdmissionDate().toString() : null;
        dto.baseSalary = e.getSalary();
        dto.grossSalary = e.getSalary();
        dto.dependents = e.getDependents();
        dto.hasHazardPay = e.getDangerousWork();
        dto.insalubrity = normalizeInsalubrity(e.getUnhealthyLevel(), e.getUnhealthyWork());
        
        // Transport Voucher logic: if value > 0 use value, else if boolean true use 1, else 0
        if (e.getTransportVoucherValue() != null && e.getTransportVoucherValue().compareTo(BigDecimal.ZERO) > 0) {
             dto.transportVoucherValue = e.getTransportVoucherValue();
        } else {
             dto.transportVoucherValue = e.getTransportVoucher() != null && e.getTransportVoucher() ? BigDecimal.ONE : BigDecimal.ZERO;
        }
        
        dto.mealVoucherDaily = e.getMealVoucherValue();
        dto.workDaysMonth = 22; // default
        dto.weeklyHours = e.getWeeklyHours();
        
        dto.hasHealthPlan = e.getHealthPlan();
        dto.healthPlanValue = e.getHealthPlanValue();
        dto.hasDentalPlan = e.getDentalPlan();
        dto.dentalPlanValue = e.getDentalPlanValue();
        dto.hasGym = e.getGym();
        dto.gymValue = e.getGymValue();
        dto.hasTimeBank = e.getTimeBank();
        dto.timeBankHours = e.getTimeBankHours();
        dto.hasOvertime = e.getOvertimeEligible();
        dto.overtimeHours = e.getOvertimeHours();
        
        return dto;
    }

    public static Employee toEntity(EmployeeDTO dto) {
        Employee e = new Employee();
        if (dto == null) return e;
        e.setId(dto.id);
        e.setFullName(dto.name);
        e.setCpf(dto.cpf);
        e.setRg("N/A");
        e.setPosition(dto.position);
        e.setAdmissionDate(dto.admissionDate != null ? LocalDate.parse(dto.admissionDate) : LocalDate.now());
        e.setSalary(dto.baseSalary != null ? dto.baseSalary : dto.grossSalary);
        e.setDependents(dto.dependents != null ? dto.dependents : 0);
        e.setWeeklyHours(dto.weeklyHours != null ? dto.weeklyHours : 40);
        // Beneficios/adicionais
        e.setDangerousWork(dto.hasHazardPay != null && dto.hasHazardPay);
        e.setDangerousPercentage(e.getDangerousWork() ? new BigDecimal("0.30") : BigDecimal.ZERO);
        e.setUnhealthyWork(dto.insalubrity != null && !"NONE".equalsIgnoreCase(dto.insalubrity));
        e.setUnhealthyLevel(dto.insalubrity);
        
        boolean hasMealVoucher = dto.mealVoucherDaily != null && dto.mealVoucherDaily.compareTo(BigDecimal.ZERO) > 0;
        e.setMealVoucher(hasMealVoucher);
        e.setMealVoucherValue(dto.mealVoucherDaily != null ? dto.mealVoucherDaily : BigDecimal.ZERO);
        
        // Transport voucher: if value > 0, set value and true. If value 0, set false (unless strictly boolean logic needed)
        // Assuming frontend sends value > 0 for active transport voucher
        boolean hasTransport = dto.transportVoucherValue != null && dto.transportVoucherValue.compareTo(BigDecimal.ZERO) > 0;
        e.setTransportVoucher(hasTransport);
        e.setTransportVoucherValue(dto.transportVoucherValue != null ? dto.transportVoucherValue : BigDecimal.ZERO);
        
        e.setHealthPlan(dto.hasHealthPlan != null && dto.hasHealthPlan);
        e.setHealthPlanValue(dto.healthPlanValue != null ? dto.healthPlanValue : BigDecimal.ZERO);
        
        e.setDentalPlan(dto.hasDentalPlan != null && dto.hasDentalPlan);
        e.setDentalPlanValue(dto.dentalPlanValue != null ? dto.dentalPlanValue : BigDecimal.ZERO);
        
        e.setGym(dto.hasGym != null && dto.hasGym);
        e.setGymValue(dto.gymValue != null ? dto.gymValue : BigDecimal.ZERO);
        
        e.setTimeBank(dto.hasTimeBank != null && dto.hasTimeBank);
        e.setTimeBankHours(dto.timeBankHours != null ? dto.timeBankHours : BigDecimal.ZERO);
        
        e.setOvertimeEligible(dto.hasOvertime != null && dto.hasOvertime);
        e.setOvertimeHours(dto.overtimeHours != null ? dto.overtimeHours : BigDecimal.ZERO);
        
        return e;
    }

    private static String normalizeInsalubrity(String level, Boolean active) {
        if (active == null || !active) return "NONE";
        if (level == null) return "NONE";
        return switch (level.toUpperCase()) {
            case "BAIXO", "LOW" -> "LOW";
            case "MEDIO", "MÉDIO", "MEDIUM" -> "MEDIUM";
            case "ALTO", "HIGH" -> "HIGH";
            default -> "NONE";
        };
    }
}
