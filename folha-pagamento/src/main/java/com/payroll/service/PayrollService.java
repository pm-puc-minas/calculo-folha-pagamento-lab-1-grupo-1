package com.payroll.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payroll.entity.PayrollCalculation;
import com.payroll.model.Employee.GrauInsalubridade;
import com.payroll.repository.PayrollCalculationRepository;

@Service
public class PayrollService implements IPayrollService {

    @Autowired
    private PayrollCalculationRepository payrollRepository;

    @Override
    public PayrollCalculation calculatePayroll(Long employeeId, String referenceMonth, Long calculatedBy) {
        Optional<PayrollCalculation> existing = payrollRepository.findByEmployeeIdAndReferenceMonth(employeeId, referenceMonth);
        if (existing.isPresent()) return existing.get();

        PayrollCalculation calculation = new PayrollCalculation();
        calculation.setReferenceMonth(referenceMonth);
        calculation.setCreatedBy(calculatedBy);

        // Simulação de dados de funcionário
        BigDecimal baseSalary = new BigDecimal("3000.00");

        BigDecimal hourlyWage = calcularSalarioHora(baseSalary, 40);
        BigDecimal dangerousBonus = calcularAdicionalPericulosidade(baseSalary);
        BigDecimal unhealthyBonus = calcularAdicionalInsalubridade(PayrollConstants.SALARIO_MINIMO, GrauInsalubridade.MEDIO);

        calculation.setHourlyWage(hourlyWage);
        calculation.setDangerousBonus(dangerousBonus);
        calculation.setUnhealthyBonus(unhealthyBonus);

        BigDecimal grossSalary = baseSalary.add(dangerousBonus).add(unhealthyBonus);
        calculation.setGrossSalary(grossSalary);

        BigDecimal inssDiscount = calcularINSS(grossSalary);
        int dependents = 0;
        BigDecimal pensionAlimony = BigDecimal.ZERO;
        BigDecimal transportVoucher = new BigDecimal("150.00");

        BigDecimal irrfDiscount = calcularIRRF(grossSalary, inssDiscount, dependents, pensionAlimony);
        BigDecimal transportDiscount = calcularDescontoValeTransporte(grossSalary, transportVoucher);

        calculation.setInssDiscount(inssDiscount);
        calculation.setIrpfDiscount(irrfDiscount);
        calculation.setTransportDiscount(transportDiscount);

        BigDecimal fgts = calcularFGTS(grossSalary);
        calculation.setFgtsValue(fgts);

        BigDecimal mealVoucher = calcularValeAlimentacao(new BigDecimal("25.00"), 22);
        calculation.setMealVoucherValue(mealVoucher);

        // líquido = bruto + adicionais + benefícios − (INSS + IRRF + FGTS + VT)
        BigDecimal totalDiscounts = inssDiscount.add(irrfDiscount).add(fgts).add(transportDiscount);
        BigDecimal netSalary = grossSalary.subtract(totalDiscounts);
        calculation.setNetSalary(netSalary);

        return payrollRepository.save(calculation);
    }


    @Override
    public BigDecimal calcularSalarioHora(BigDecimal salarioBruto, int horasSemanais) {
        if (salarioBruto == null || horasSemanais <= 0) return BigDecimal.ZERO;
        BigDecimal horasMensais = new BigDecimal(horasSemanais).multiply(PayrollConstants.WEEKS_PER_MONTH);
        return salarioBruto.divide(horasMensais, 2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularAdicionalPericulosidade(BigDecimal salarioBase) {
        if (salarioBase == null) return BigDecimal.ZERO;
        return salarioBase.multiply(PayrollConstants.DANGER_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularAdicionalInsalubridade(BigDecimal salarioMinimo, GrauInsalubridade grau) {
        if (salarioMinimo == null || grau == null || grau == GrauInsalubridade.NENHUM) return BigDecimal.ZERO;

        BigDecimal percentual = switch (grau) {
            case BAIXO -> PayrollConstants.INSALUBRITY_LOW;
            case MEDIO -> PayrollConstants.INSALUBRITY_MEDIUM;
            case ALTO -> PayrollConstants.INSALUBRITY_HIGH;
            default -> BigDecimal.ZERO;
        };
        return salarioMinimo.multiply(percentual).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularDescontoValeTransporte(BigDecimal salarioBruto, BigDecimal valorEntregue) {
        if (salarioBruto == null || valorEntregue == null) return BigDecimal.ZERO;
        BigDecimal descontoMaximo = salarioBruto.multiply(PayrollConstants.TRANSPORTE_RATE);
        return valorEntregue.min(descontoMaximo).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularValeAlimentacao(BigDecimal valorDiario, int diasTrabalhados) {
        if (valorDiario == null || diasTrabalhados <= 0) return BigDecimal.ZERO;
        return valorDiario.multiply(new BigDecimal(diasTrabalhados)).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularINSS(BigDecimal salarioContribuicao) {
        if (salarioContribuicao == null || salarioContribuicao.compareTo(BigDecimal.ZERO) <= 0)
            return BigDecimal.ZERO;

        BigDecimal totalDesconto = BigDecimal.ZERO;
        BigDecimal salarioRestante = salarioContribuicao;

        for (int i = 0; i < PayrollConstants.INSS_LIMITS.length && salarioRestante.compareTo(BigDecimal.ZERO) > 0; i++) {
            BigDecimal limite = PayrollConstants.INSS_LIMITS[i];
            BigDecimal limiteAnterior = i > 0 ? PayrollConstants.INSS_LIMITS[i - 1] : BigDecimal.ZERO;
            BigDecimal valorTributavel = salarioRestante.min(limite.subtract(limiteAnterior));

            if (valorTributavel.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal desconto = valorTributavel.multiply(PayrollConstants.INSS_RATES[i]);
                totalDesconto = totalDesconto.add(desconto);
                salarioRestante = salarioRestante.subtract(valorTributavel);
            }
        }

        return totalDesconto.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularFGTS(BigDecimal baseCalculoFGTS) {
        if (baseCalculoFGTS == null || baseCalculoFGTS.compareTo(BigDecimal.ZERO) <= 0)
            return BigDecimal.ZERO;
        return baseCalculoFGTS.multiply(PayrollConstants.FGTS_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularIRRF(BigDecimal salarioBruto, BigDecimal descontoINSS, int numDependentes, BigDecimal pensaoAlimenticia) {
        if (salarioBruto == null || descontoINSS == null) return BigDecimal.ZERO;

        BigDecimal deducaoDependentes = PayrollConstants.DEDUCAO_DEPENDENTE.multiply(new BigDecimal(numDependentes));
        BigDecimal pensao = pensaoAlimenticia != null ? pensaoAlimenticia : BigDecimal.ZERO;
        BigDecimal baseCalculo = salarioBruto.subtract(descontoINSS).subtract(deducaoDependentes).subtract(pensao);

        if (baseCalculo.compareTo(PayrollConstants.IRPF_ISENTO) <= 0) return BigDecimal.ZERO;

        BigDecimal totalIRRF = BigDecimal.ZERO;
        BigDecimal baseRestante = baseCalculo;

        for (int i = 0; i < PayrollConstants.IRPF_LIMITS.length && baseRestante.compareTo(BigDecimal.ZERO) > 0; i++) {
            BigDecimal limite = PayrollConstants.IRPF_LIMITS[i];
            BigDecimal limiteAnterior = i > 0 ? PayrollConstants.IRPF_LIMITS[i - 1] : BigDecimal.ZERO;

            if (baseCalculo.compareTo(limite) > 0) {
                BigDecimal valorTributavel = limite.subtract(limiteAnterior);
                totalIRRF = totalIRRF.add(valorTributavel.multiply(PayrollConstants.IRPF_RATES[i]));
                baseRestante = baseRestante.subtract(valorTributavel);
            } else {
                BigDecimal valorTributavel = baseCalculo.subtract(limiteAnterior);
                totalIRRF = totalIRRF.add(valorTributavel.multiply(PayrollConstants.IRPF_RATES[i]));
                break;
            }
        }

        if (baseRestante.compareTo(BigDecimal.ZERO) > 0) {
            totalIRRF = totalIRRF.add(baseRestante.multiply(PayrollConstants.IRPF_RATES[PayrollConstants.IRPF_RATES.length - 1]));
        }

        return totalIRRF.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public List<PayrollCalculation> getEmployeePayrolls(Long employeeId) {
        return payrollRepository.findByEmployeeId(employeeId);
    }

    @Override
    public List<PayrollCalculation> getAllPayrolls() {
        return payrollRepository.findAll();
    }
}