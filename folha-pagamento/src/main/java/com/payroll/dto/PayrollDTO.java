package com.payroll.dto;

import com.payroll.entity.PayrollCalculation;
import com.payroll.entity.User;
import java.math.BigDecimal;

public class PayrollDTO {
    public Long id;
    public Long employeeId;
    public String employeeName;
    public String month;
    public BigDecimal hourlyRate;
    public BigDecimal totalEarnings;
    public BigDecimal totalDeductions;
    public BigDecimal netSalary;
    public BigDecimal hazardPayValue;
    public BigDecimal insalubrityValue;
    public BigDecimal mealVoucherValue;
    public BigDecimal transportVoucherDiscount;
    public BigDecimal inssDiscount;
    public BigDecimal fgtsValue;
    public BigDecimal irrfDiscount;
    public BigDecimal healthPlanDiscount;
    public BigDecimal dentalPlanDiscount;
    public BigDecimal gymDiscount;
    public BigDecimal overtimeValue;
    public BigDecimal inssBase;
    public BigDecimal fgtsBase;
    public BigDecimal irrfBase;
    public String calculatedAt;
    public UserRef generatedBy;

    public static class UserRef {
        public Long id;
        public String name;
        public String email;
        public String role;

        public static UserRef fromUser(User u) {
            if (u == null) return null;
            UserRef ref = new UserRef();
            ref.id = u.getId();
            ref.name = u.getUsername();
            ref.email = u.getEmail();
            ref.role = u.getRole() != null ? u.getRole().name() : null;
            return ref;
        }
    }

    public static PayrollDTO fromEntity(PayrollCalculation p, User creator) {
        PayrollDTO dto = new PayrollDTO();
        if (p == null) return dto;
        dto.id = p.getId();
        dto.employeeId = p.getEmployee() != null ? p.getEmployee().getId() : null;
        dto.employeeName = p.getEmployee() != null ? p.getEmployee().getFullName() : null;
        dto.month = p.getReferenceMonth();
        dto.hourlyRate = p.getHourlyWage();
        // Proventos total (Salário Bruto já deve incluir todos os adicionais)
        dto.totalEarnings = nz(p.getGrossSalary());
        
        // Descontos total (INSS + IRRF + VT + FGTS + Beneficios)
        BigDecimal inss = nz(p.getInssDiscount());
        BigDecimal irrf = nz(p.getIrpfDiscount());
        BigDecimal vt = nz(p.getTransportDiscount());
        BigDecimal fgts = nz(p.getFgtsValue());
        BigDecimal health = nz(p.getHealthPlanDiscount());
        BigDecimal dental = nz(p.getDentalPlanDiscount());
        BigDecimal gym = nz(p.getGymDiscount());
        
        dto.totalDeductions = inss.add(irrf).add(vt).add(fgts).add(health).add(dental).add(gym);
        
        dto.netSalary = p.getNetSalary();
        dto.hazardPayValue = p.getDangerousBonus();
        dto.insalubrityValue = p.getUnhealthyBonus();
        dto.mealVoucherValue = p.getMealVoucherValue();
        dto.transportVoucherDiscount = p.getTransportDiscount();
        dto.inssDiscount = p.getInssDiscount();
        dto.fgtsValue = p.getFgtsValue();
        dto.irrfDiscount = p.getIrpfDiscount();
        
        dto.healthPlanDiscount = p.getHealthPlanDiscount();
        dto.dentalPlanDiscount = p.getDentalPlanDiscount();
        dto.gymDiscount = p.getGymDiscount();
        dto.overtimeValue = p.getOvertimeValue();

        dto.inssBase = p.getGrossSalary();
        dto.fgtsBase = p.getGrossSalary();
        dto.irrfBase = p.getGrossSalary() != null && p.getInssDiscount() != null
                ? p.getGrossSalary().subtract(p.getInssDiscount())
                : p.getGrossSalary();
        dto.calculatedAt = p.getCreatedAt() != null ? p.getCreatedAt().toString() : null;
        dto.generatedBy = UserRef.fromUser(creator);
        return dto;
    }

    private static BigDecimal nz(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }
}