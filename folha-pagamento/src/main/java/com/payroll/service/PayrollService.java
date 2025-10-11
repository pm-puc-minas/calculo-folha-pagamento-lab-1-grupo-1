package com.payroll.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payroll.entity.PayrollCalculation;
import com.payroll.repository.PayrollCalculationRepository;

@Service
public class PayrollService implements IPayrollService {

    @Autowired
    private PayrollCalculationRepository payrollRepository;

    // Tabelas de INSS 2024
    private static final BigDecimal[] INSS_LIMITS = {
            new BigDecimal("1412.00"),
            new BigDecimal("2666.68"),
            new BigDecimal("4000.03"),
            new BigDecimal("7786.02")
    };

    private static final BigDecimal[] INSS_RATES = {
            new BigDecimal("0.075"),
            new BigDecimal("0.09"),
            new BigDecimal("0.12"),
            new BigDecimal("0.14")
    };

    // Tabela de IRPF 2024
    private static final BigDecimal[] IRPF_LIMITS = {
            new BigDecimal("2259.20"),
            new BigDecimal("2826.65"),
            new BigDecimal("3751.05"),
            new BigDecimal("4664.68")
    };

    private static final BigDecimal[] IRPF_RATES = {
            new BigDecimal("0.075"),
            new BigDecimal("0.15"),
            new BigDecimal("0.225"),
            new BigDecimal("0.275")
    };

    private static final BigDecimal[] IRPF_DEDUCTIONS = {
            new BigDecimal("169.44"),
            new BigDecimal("381.44"),
            new BigDecimal("662.77"),
            new BigDecimal("896.00")
    };

    public PayrollCalculation calculatePayroll(Long employeeId, String referenceMonth, Long calculatedBy) {
        // Verificar se já existe cálculo para o mês
        Optional<PayrollCalculation> existing = payrollRepository.findByEmployeeIdAndReferenceMonth(employeeId, referenceMonth);
        if (existing.isPresent()) {
            return existing.get();
        }

        PayrollCalculation calculation = new PayrollCalculation();
        calculation.setReferenceMonth(referenceMonth);
        calculation.setCreatedBy(calculatedBy);

        // Buscar dados do funcionário seria feito aqui via EmployeeService
        // Para demonstração, vamos usar valores fictícios
        BigDecimal baseSalary = new BigDecimal("3000.00");

        // Calcular salário hora
        BigDecimal hourlyWage = calculateHourlyWage(baseSalary, 40);
        calculation.setHourlyWage(hourlyWage);

        // Calcular adicionais
        BigDecimal dangerousBonus = calculateDangerousBonus(baseSalary, new BigDecimal("30"));
        BigDecimal unhealthyBonus = calculateUnhealthyBonus(baseSalary, "MEDIO");

        calculation.setDangerousBonus(dangerousBonus);
        calculation.setUnhealthyBonus(unhealthyBonus);

        // Salário bruto
        BigDecimal grossSalary = baseSalary.add(dangerousBonus).add(unhealthyBonus);
        calculation.setGrossSalary(grossSalary);

        // Calcular descontos
        BigDecimal inssDiscount = calculateINSS(grossSalary);
        BigDecimal irpfDiscount = calculateIRPF(grossSalary, inssDiscount);
        BigDecimal transportDiscount = calculateTransportDiscount(grossSalary, true);

        calculation.setInssDiscount(inssDiscount);
        calculation.setIrpfDiscount(irpfDiscount);
        calculation.setTransportDiscount(transportDiscount);

        // FGTS
        BigDecimal fgts = grossSalary.multiply(new BigDecimal("0.08")).setScale(2, RoundingMode.HALF_UP);
        calculation.setFgtsValue(fgts);

        // Vale refeição
        BigDecimal mealVoucher = new BigDecimal("25.00").multiply(new BigDecimal("22"));
        calculation.setMealVoucherValue(mealVoucher);

        // Salário líquido
        BigDecimal totalDiscounts = inssDiscount.add(irpfDiscount).add(transportDiscount);
        BigDecimal netSalary = grossSalary.subtract(totalDiscounts);
        calculation.setNetSalary(netSalary);

        return payrollRepository.save(calculation);
    }

    private BigDecimal calculateHourlyWage(BigDecimal salary, int weeklyHours) {
        BigDecimal monthlyHours = new BigDecimal(weeklyHours * 4.33);
        return salary.divide(monthlyHours, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDangerousBonus(BigDecimal salary, BigDecimal percentage) {
        return salary.multiply(percentage.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateUnhealthyBonus(BigDecimal salary, String level) {
        BigDecimal percentage = switch (level) {
            case "MINIMO" -> new BigDecimal("10");
            case "MEDIO" -> new BigDecimal("20");
            case "MAXIMO" -> new BigDecimal("40");
            default -> BigDecimal.ZERO;
        };
        return salary.multiply(percentage.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateINSS(BigDecimal grossSalary) {
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal remainingSalary = grossSalary;

        for (int i = 0; i < INSS_LIMITS.length && remainingSalary.compareTo(BigDecimal.ZERO) > 0; i++) {
            BigDecimal limit = INSS_LIMITS[i];
            BigDecimal previousLimit = i > 0 ? INSS_LIMITS[i - 1] : BigDecimal.ZERO;
            BigDecimal taxableAmount = remainingSalary.min(limit.subtract(previousLimit));

            if (taxableAmount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discount = taxableAmount.multiply(INSS_RATES[i]);
                totalDiscount = totalDiscount.add(discount);
                remainingSalary = remainingSalary.subtract(taxableAmount);
            }
        }

        return totalDiscount.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateIRPF(BigDecimal grossSalary, BigDecimal inssDiscount) {
        BigDecimal taxBase = grossSalary.subtract(inssDiscount);

        for (int i = IRPF_LIMITS.length - 1; i >= 0; i--) {
            if (taxBase.compareTo(IRPF_LIMITS[i]) > 0) {
                BigDecimal tax = taxBase.multiply(IRPF_RATES[i]).subtract(IRPF_DEDUCTIONS[i]);
                return tax.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
            }
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal calculateTransportDiscount(BigDecimal grossSalary, boolean hasTransportVoucher) {
        if (!hasTransportVoucher) {
            return BigDecimal.ZERO;
        }
        return grossSalary.multiply(new BigDecimal("0.06")).setScale(2, RoundingMode.HALF_UP);
    }

    public List<PayrollCalculation> getEmployeePayrolls(Long employeeId) {
        return payrollRepository.findByEmployeeId(employeeId);
    }

    public List<PayrollCalculation> getAllPayrolls() {
        return payrollRepository.findAll();
    }
}