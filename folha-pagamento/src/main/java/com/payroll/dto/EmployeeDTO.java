package com.payroll.dto;

import com.payroll.entity.Employee;
import java.math.BigDecimal;

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

    public static EmployeeDTO fromEntity(Employee e) {
        EmployeeDTO dto = new EmployeeDTO();
        if (e == null) return dto;
        dto.id = e.getId();
        dto.name = e.getFullName();
        dto.cpf = e.getCpf();
        dto.position = e.getPosition();
        dto.department = ""; // Sem campo dedicado, mantido vazio
        dto.admissionDate = e.getAdmissionDate() != null ? e.getAdmissionDate().toString() : null;
        dto.baseSalary = e.getSalary();
        dto.dependents = e.getDependents();
        dto.hasHazardPay = e.getDangerousWork();
        // Mapeia nível de insalubridade
        String level = e.getUnhealthyLevel();
        dto.insalubrity = level == null ? "NONE" : switch (level.toUpperCase()) {
            case "BAIXO", "LOW" -> "LOW";
            case "MEDIO", "MÉDIO", "MEDIUM" -> "MEDIUM";
            case "ALTO", "HIGH" -> "HIGH";
            default -> "NONE";
        };
        // Sem valor armazenado para VT; default 0
        dto.transportVoucherValue = BigDecimal.ZERO;
        // Usa valor de vale alimentação existente (assumido diário)
        dto.mealVoucherDaily = e.getMealVoucherValue();
        dto.workDaysMonth = 22; // padrão
        dto.weeklyHours = e.getWeeklyHours();
        return dto;
    }
}