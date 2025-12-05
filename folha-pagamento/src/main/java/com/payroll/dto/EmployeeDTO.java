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
        dto.transportVoucherValue = e.getTransportVoucher() != null && e.getTransportVoucher() ? BigDecimal.ZERO : BigDecimal.ZERO;
        dto.mealVoucherDaily = e.getMealVoucherValue();
        dto.workDaysMonth = 22; // default
        dto.weeklyHours = e.getWeeklyHours();
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
        e.setMealVoucher(true);
        e.setMealVoucherValue(dto.mealVoucherDaily != null ? dto.mealVoucherDaily : BigDecimal.ZERO);
        e.setTransportVoucher(dto.transportVoucherValue != null && dto.transportVoucherValue.compareTo(BigDecimal.ZERO) > 0);
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
