package com.payroll.dto;

/*
 * Objeto de Transferência de Dados (DTO) para Funcionários.
 * Responsável por transportar dados entre a camada de persistência e a interface (frontend),
 * centralizando as regras de conversão de tipos e formatação de dados sensíveis ou complexos.
 */

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

    // Compatibilidade com frontend: alias para baseSalary
    public BigDecimal grossSalary;

    // Converter entidade de banco de dados para DTO (Output)
    public static EmployeeDTO fromEntity(Employee e) {
        EmployeeDTO dto = new EmployeeDTO();
        if (e == null) return dto;

        // Mapeamento de dados cadastrais básicos
        dto.id = e.getId();
        dto.name = e.getFullName();
        dto.cpf = e.getCpf();
        dto.position = e.getPosition();
        dto.department = ""; // Campo reservado para futura implementação
        dto.admissionDate = e.getAdmissionDate() != null ? e.getAdmissionDate().toString() : null;
        dto.baseSalary = e.getSalary();
        dto.grossSalary = e.getSalary();
        dto.dependents = e.getDependents();

        // Mapeamento de adicionais de trabalho (Periculosidade/Insalubridade)
        dto.hasHazardPay = e.getDangerousWork();
        dto.insalubrity = normalizeInsalubrity(e.getUnhealthyLevel(), e.getUnhealthyWork());
        
        // Lógica do Vale Transporte: Prioriza o valor monetário; se nulo, verifica o booleano
        if (e.getTransportVoucherValue() != null && e.getTransportVoucherValue().compareTo(BigDecimal.ZERO) > 0) {
             dto.transportVoucherValue = e.getTransportVoucherValue();
        } else {
             dto.transportVoucherValue = e.getTransportVoucher() != null && e.getTransportVoucher() ? BigDecimal.ONE : BigDecimal.ZERO;
        }
        
        // Definição de jornada e benefícios alimentares
        dto.mealVoucherDaily = e.getMealVoucherValue();
        dto.workDaysMonth = 22; // Valor padrão de dias úteis
        dto.weeklyHours = e.getWeeklyHours();
        
        // Mapeamento dos demais benefícios (Saúde, Academia, Banco de Horas)
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

    // Converter DTO recebido da API para entidade de banco (Input)
    public static Employee toEntity(EmployeeDTO dto) {
        Employee e = new Employee();
        if (dto == null) return e;

        // Preenchimento de dados cadastrais e conversão de data
        e.setId(dto.id);
        e.setFullName(dto.name);
        e.setCpf(dto.cpf);
        e.setRg("N/A"); // Valor default pois não vem do formulário simplificado
        e.setPosition(dto.position);
        e.setAdmissionDate(dto.admissionDate != null ? LocalDate.parse(dto.admissionDate) : LocalDate.now());
        e.setSalary(dto.baseSalary != null ? dto.baseSalary : dto.grossSalary);
        e.setDependents(dto.dependents != null ? dto.dependents : 0);
        e.setWeeklyHours(dto.weeklyHours != null ? dto.weeklyHours : 40);

        // Configuração de Periculosidade e Insalubridade
        e.setDangerousWork(dto.hasHazardPay != null && dto.hasHazardPay);
        e.setDangerousPercentage(e.getDangerousWork() ? new BigDecimal("0.30") : BigDecimal.ZERO);
        e.setUnhealthyWork(dto.insalubrity != null && !"NONE".equalsIgnoreCase(dto.insalubrity));
        e.setUnhealthyLevel(dto.insalubrity);
        
        // Configuração do Vale Refeição
        boolean hasMealVoucher = dto.mealVoucherDaily != null && dto.mealVoucherDaily.compareTo(BigDecimal.ZERO) > 0;
        e.setMealVoucher(hasMealVoucher);
        e.setMealVoucherValue(dto.mealVoucherDaily != null ? dto.mealVoucherDaily : BigDecimal.ZERO);
        
        // Configuração do Vale Transporte baseada na existência de valor monetário
        boolean hasTransport = dto.transportVoucherValue != null && dto.transportVoucherValue.compareTo(BigDecimal.ZERO) > 0;
        e.setTransportVoucher(hasTransport);
        e.setTransportVoucherValue(dto.transportVoucherValue != null ? dto.transportVoucherValue : BigDecimal.ZERO);
        
        // Configuração dos demais planos e benefícios
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

    // Método auxiliar para padronizar o nível de insalubridade
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