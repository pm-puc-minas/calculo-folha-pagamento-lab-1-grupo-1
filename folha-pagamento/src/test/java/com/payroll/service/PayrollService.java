package com.payroll.service;

import com.payroll.entity.PayrollCalculation;
import com.payroll.repository.PayrollCalculationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class PayrollService {

    @Autowired
    private PayrollCalculationRepository payrollRepository;

    // Tabelas de INSS 2024
    public PayrollCalculation calculatePayroll(Long employeeId, String referenceMonth, Long companyId) {
        Optional<PayrollCalculation> existing = payrollRepository.findByEmployeeIdAndReferenceMonth(employeeId, referenceMonth);
        if (existing.isPresent()) {
            return existing.get();
        }

        PayrollCalculation calculation = new PayrollCalculation();
    
        calculation.setReferenceMonth(referenceMonth);

        // Salário base
        BigDecimal grossSalary = new BigDecimal("3000.00");
        calculation.setGrossSalary(grossSalary);

        // Cálculo do valor da hora (baseado em 4.33 semanas por mês, 44h semanais)
        BigDecimal hourlyWage = grossSalary
                .divide(new BigDecimal("4.33"), 2, RoundingMode.HALF_UP)
                .divide(new BigDecimal("44"), 2, RoundingMode.HALF_UP);
        calculation.setHourlyWage(hourlyWage);

        // Adicionais
        BigDecimal dangerousBonus = new BigDecimal("900.00");
        BigDecimal unhealthyBonus = new BigDecimal("600.00");
        calculation.setDangerousBonus(dangerousBonus);
        calculation.setUnhealthyBonus(unhealthyBonus);

        // Descontos com proteção contra negativos
        BigDecimal inssDiscount = calculateINSS(grossSalary);
        if (inssDiscount.compareTo(BigDecimal.ZERO) < 0) inssDiscount = BigDecimal.ZERO;

        BigDecimal irpfDiscount = calculateIRPF(grossSalary);
        if (irpfDiscount.compareTo(BigDecimal.ZERO) < 0) irpfDiscount = BigDecimal.ZERO;

        BigDecimal transportDiscount = calculateTransport(grossSalary);
        if (transportDiscount.compareTo(BigDecimal.ZERO) < 0) transportDiscount = BigDecimal.ZERO;

        calculation.setInssDiscount(inssDiscount);
        calculation.setIrpfDiscount(irpfDiscount);
        calculation.setTransportDiscount(transportDiscount);

        // Benefícios
        BigDecimal fgtsValue = calculateFGTS(grossSalary);
        calculation.setFgtsValue(fgtsValue);

        BigDecimal mealVoucherValue = new BigDecimal("550.00");
        calculation.setMealVoucherValue(mealVoucherValue);

        // Cálculo final do salário líquido
        BigDecimal totalDiscounts = inssDiscount.add(irpfDiscount).add(transportDiscount);
        BigDecimal totalAdditions = dangerousBonus.add(unhealthyBonus);
        BigDecimal netSalary = grossSalary.add(totalAdditions).subtract(totalDiscounts);
        calculation.setNetSalary(netSalary);

        // Salva no repositório
        return payrollRepository.save(calculation);
    }

   
    public List<PayrollCalculation> getEmployeePayrolls(Long employeeId) {
        return payrollRepository.findByEmployeeId(employeeId);
    }

    
    public List<PayrollCalculation> getAllPayrolls() {
        return payrollRepository.findAll();
    }

    // MÉTODOS DE CÁLCULO =

    private BigDecimal calculateINSS(BigDecimal grossSalary) {
        // Exemplo simples: 11% do salário bruto
        BigDecimal inss = grossSalary.multiply(new BigDecimal("0.11"));
        return inss.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateIRPF(BigDecimal grossSalary) {
        // Exemplo simples: 6% do salário bruto
        BigDecimal irpf = grossSalary.multiply(new BigDecimal("0.06"));
        return irpf.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTransport(BigDecimal grossSalary) {
        // Exemplo: 6% do salário bruto
        BigDecimal transport = grossSalary.multiply(new BigDecimal("0.06"));
        return transport.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateFGTS(BigDecimal grossSalary) {
        // Exemplo: 8% do salário bruto
        BigDecimal fgts = grossSalary.multiply(new BigDecimal("0.08"));
        return fgts.setScale(2, RoundingMode.HALF_UP);
    }
}
