package com.payroll.dto;

import com.payroll.entity.PayrollCalculation;
import com.payroll.entity.User;
import java.math.BigDecimal;

// DTO que representa o holerite/contracheque detalhado.
// Contém todos os valores calculados, bases e totais para exibição.
public class PayrollDTO {
    
    // Identificação do registro de cálculo e do funcionário
    public Long id;
    public Long employeeId;
    public String employeeName;
    public String month; // Mês de referência (ex: "10/2025")
    
    // Totais financeiros (Resumo do Holerite)
    public BigDecimal hourlyRate;
    public BigDecimal totalEarnings;   // Total de Proventos
    public BigDecimal totalDeductions; // Total de Descontos
    public BigDecimal netSalary;       // Salário Líquido
    
    // Componentes de Proventos (Adicionais e Benefícios recebidos)
    public BigDecimal hazardPayValue;   // Periculosidade
    public BigDecimal insalubrityValue; // Insalubridade
    public BigDecimal mealVoucherValue; // Valor do VA (se pago em dinheiro/folha)
    public BigDecimal overtimeValue;    // Horas extras
    
    // Componentes de Descontos Legais (Obrigatórios)
    public BigDecimal transportVoucherDiscount; // VT (6%)
    public BigDecimal inssDiscount;             // INSS
    public BigDecimal fgtsValue;                // FGTS (não é descontado, mas aparece informativo)
    public BigDecimal irrfDiscount;             // Imposto de Renda
    
    // Componentes de Descontos de Benefícios (Planos)
    public BigDecimal healthPlanDiscount;
    public BigDecimal dentalPlanDiscount;
    public BigDecimal gymDiscount;
    
    // Bases de Cálculo (Informativo para conferência de impostos)
    public BigDecimal inssBase;
    public BigDecimal fgtsBase;
    public BigDecimal irrfBase;
    
    // Auditoria (Quando foi calculado e por quem)
    public String calculatedAt;
    public UserRef generatedBy;

    // Sub-classe DTO para representar o usuário que gerou o cálculo de forma resumida
    public static class UserRef {
        public Long id;
        public String name;
        public String email;
        public String role;

        // Converte User (Entity) para UserRef (DTO)
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

    // Método Factory: Converte a entidade do banco (PayrollCalculation) para este DTO
    public static PayrollDTO fromEntity(PayrollCalculation p, User creator) {
        PayrollDTO dto = new PayrollDTO();
        if (p == null) return dto;

        // Mapeamento de identificação
        dto.id = p.getId();
        dto.employeeId = p.getEmployee() != null ? p.getEmployee().getId() : null;
        dto.employeeName = p.getEmployee() != null ? p.getEmployee().getFullName() : null;
        dto.month = p.getReferenceMonth();
        dto.hourlyRate = p.getHourlyWage();

        // Proventos total (Salário Bruto já deve incluir todos os adicionais)
        dto.totalEarnings = nz(p.getGrossSalary());
        
        // Cálculo do Total de Descontos (Soma manual de todos os descontos aplicáveis)
        BigDecimal inss = nz(p.getInssDiscount());
        BigDecimal irrf = nz(p.getIrpfDiscount());
        BigDecimal vt = nz(p.getTransportDiscount());
        BigDecimal fgts = nz(p.getFgtsValue()); // Atenção: FGTS geralmente não subtrai do líquido, verifique a regra de negócio aqui.
        BigDecimal health = nz(p.getHealthPlanDiscount());
        BigDecimal dental = nz(p.getDentalPlanDiscount());
        BigDecimal gym = nz(p.getGymDiscount());
        
        dto.totalDeductions = inss.add(irrf).add(vt).add(fgts).add(health).add(dental).add(gym);
        
        // Mapeamento dos valores líquidos e detalhados
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

        // Definição das Bases de Cálculo
        dto.inssBase = p.getGrossSalary();
        dto.fgtsBase = p.getGrossSalary();
        // Base IRRF = Bruto - INSS (Cálculo simplificado)
        dto.irrfBase = p.getGrossSalary() != null && p.getInssDiscount() != null
                ? p.getGrossSalary().subtract(p.getInssDiscount())
                : p.getGrossSalary();

        // Dados de auditoria
        dto.calculatedAt = p.getCreatedAt() != null ? p.getCreatedAt().toString() : null;
        dto.generatedBy = UserRef.fromUser(creator);
        
        return dto;
    }
    

    // Utilitário: Trata valores nulos retornando Zero (evita NullPointerException em somas) 
    private static BigDecimal nz(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }
}