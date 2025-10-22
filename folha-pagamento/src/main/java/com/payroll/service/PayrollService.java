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

  // Tabela de INSS 2024
private static final BigDecimal[] INSS_LIMITS = {
    new BigDecimal("1412.00"),  // até 1.412,00 → 7,5%
    new BigDecimal("2666.68"),  // de 1.412,01 até 2.666,68 → 9%
    new BigDecimal("4000.03"),  // de 2.666,69 até 4.000,03 → 12%
    new BigDecimal("7786.02")   // de 4.000,04 até 7.786,02 → 14%
};

private static final BigDecimal[] INSS_RATES = {
    new BigDecimal("0.075"),  // 7,5%
    new BigDecimal("0.09"),   // 9%
    new BigDecimal("0.12"),   // 12%
    new BigDecimal("0.14")    // 14%
};


   // Tabela de IRPF 2024 - Cálculo progressivo por faixa
private static final BigDecimal IRPF_ISENTO = new BigDecimal("2259.20");

private static final BigDecimal[] IRPF_LIMITS = {
        new BigDecimal("2259.20"),
        new BigDecimal("2826.65"),
        new BigDecimal("3751.05"),
        new BigDecimal("4664.68")
};

private static final BigDecimal[] IRPF_RATES = {
        new BigDecimal("0.0"),      // Isento
        new BigDecimal("0.075"),    // 7,5%
        new BigDecimal("0.15"),     // 15%
        new BigDecimal("0.225"),    // 22,5%
        new BigDecimal("0.275")     // 27,5%
};


    private static final BigDecimal DEDUCAO_DEPENDENTE = new BigDecimal("189.59");
    private static final BigDecimal SALARIO_MINIMO = new BigDecimal("1412.00");

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
        BigDecimal hourlyWage = calcularSalarioHora(baseSalary, 40);
        calculation.setHourlyWage(hourlyWage);

        // Calcular adicionais
        BigDecimal dangerousBonus = calcularAdicionalPericulosidade(baseSalary);
        BigDecimal unhealthyBonus = calcularAdicionalInsalubridade(SALARIO_MINIMO, GrauInsalubridade.MEDIO);

        calculation.setDangerousBonus(dangerousBonus);
        calculation.setUnhealthyBonus(unhealthyBonus);

        // Salário bruto
        BigDecimal grossSalary = baseSalary.add(dangerousBonus).add(unhealthyBonus);
        calculation.setGrossSalary(grossSalary);

        // Calcular descontos
        BigDecimal inssDiscount = calcularINSS(grossSalary);
        
        // Para demonstração, usando valores fictícios de dependentes e pensão
        int dependents = 0;
        BigDecimal pensionAlimony = BigDecimal.ZERO;
        BigDecimal transportVoucherValue = new BigDecimal("150.00");
        
        BigDecimal irpfDiscount = calcularIRRF(grossSalary, inssDiscount, dependents, pensionAlimony);
        BigDecimal transportDiscount = calcularDescontoValeTransporte(grossSalary, transportVoucherValue);

        calculation.setInssDiscount(inssDiscount);
        calculation.setIrpfDiscount(irpfDiscount);
        calculation.setTransportDiscount(transportDiscount);

        // FGTS
        BigDecimal fgts = calcularFGTS(grossSalary);
        calculation.setFgtsValue(fgts);

        // Vale refeição
        BigDecimal mealVoucher = calcularValeAlimentacao(new BigDecimal("25.00"), 22);
        calculation.setMealVoucherValue(mealVoucher);

        // Salário líquido
        BigDecimal totalDiscounts = inssDiscount.add(irpfDiscount).add(transportDiscount);
        BigDecimal netSalary = grossSalary.subtract(totalDiscounts);
        calculation.setNetSalary(netSalary);

        return payrollRepository.save(calculation);
    }

    @Override
    public BigDecimal calcularSalarioHora(BigDecimal salarioBruto, int horasSemanais) {
        if (salarioBruto == null || horasSemanais <= 0) {
            return BigDecimal.ZERO;
        }
        // Cálculo baseado em 4.33 semanas por mês (52 semanas / 12 meses)
        BigDecimal horasMensais = new BigDecimal(horasSemanais).multiply(new BigDecimal("4.33"));
        return salarioBruto.divide(horasMensais, 2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularAdicionalPericulosidade(BigDecimal salarioBase) {
        if (salarioBase == null) {
            return BigDecimal.ZERO;
        }
        // Adicional de periculosidade é 30% do salário base
        return salarioBase.multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularAdicionalInsalubridade(BigDecimal salarioMinimo, GrauInsalubridade grau) {
        if (salarioMinimo == null || grau == null || grau == GrauInsalubridade.NENHUM) {
            return BigDecimal.ZERO;
        }
        BigDecimal percentual = switch (grau) {
            case BAIXO -> new BigDecimal("0.10");   // 10%
            case MEDIO -> new BigDecimal("0.20");   // 20%
            case ALTO -> new BigDecimal("0.40");    // 40%
            default -> BigDecimal.ZERO;
        };
        return salarioMinimo.multiply(percentual).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularDescontoValeTransporte(BigDecimal salarioBruto, BigDecimal valorEntregue) {
        if (salarioBruto == null || valorEntregue == null) {
            return BigDecimal.ZERO;
        }
        // Desconto máximo de 6% do salário bruto
        BigDecimal descontoMaximo = salarioBruto.multiply(new BigDecimal("0.06"));
        // O desconto é o menor entre o valor entregue e 6% do salário
        return valorEntregue.min(descontoMaximo).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularValeAlimentacao(BigDecimal valorDiario, int diasTrabalhados) {
        if (valorDiario == null || diasTrabalhados <= 0) {
            return BigDecimal.ZERO;
        }
        return valorDiario.multiply(new BigDecimal(diasTrabalhados)).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularINSS(BigDecimal salarioContribuicao) {
        if (salarioContribuicao == null || salarioContribuicao.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalDesconto = BigDecimal.ZERO;
        BigDecimal salarioRestante = salarioContribuicao;

        // Cálculo progressivo por faixa
        for (int i = 0; i < INSS_LIMITS.length && salarioRestante.compareTo(BigDecimal.ZERO) > 0; i++) {
            BigDecimal limite = INSS_LIMITS[i];
            BigDecimal limiteAnterior = i > 0 ? INSS_LIMITS[i - 1] : BigDecimal.ZERO;
            BigDecimal valorTributavel = salarioRestante.min(limite.subtract(limiteAnterior));

            if (valorTributavel.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal desconto = valorTributavel.multiply(INSS_RATES[i]);
                totalDesconto = totalDesconto.add(desconto);
                salarioRestante = salarioRestante.subtract(valorTributavel);
            }
        }

        return totalDesconto.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularFGTS(BigDecimal baseCalculoFGTS) {
        if (baseCalculoFGTS == null || baseCalculoFGTS.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        // FGTS é sempre 8% do salário bruto
        return baseCalculoFGTS.multiply(new BigDecimal("0.08")).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularIRRF(BigDecimal salarioBruto, BigDecimal descontoINSS, int numDependentes, BigDecimal pensaoAlimenticia) {
        if (salarioBruto == null || descontoINSS == null) {
            return BigDecimal.ZERO;
        }

        // Base de cálculo: salário bruto - INSS - (dependentes * 189.59) - pensão alimentícia
        BigDecimal deducaoDependentes = DEDUCAO_DEPENDENTE.multiply(new BigDecimal(numDependentes));
        BigDecimal pensao = (pensaoAlimenticia != null) ? pensaoAlimenticia : BigDecimal.ZERO;
        BigDecimal baseCalculo = salarioBruto.subtract(descontoINSS).subtract(deducaoDependentes).subtract(pensao);

        // Verificar se está isento
        if (baseCalculo.compareTo(IRPF_ISENTO) <= 0) {
            return BigDecimal.ZERO;
        }

        // Cálculo progressivo por faixa (similar ao INSS)
        BigDecimal totalIRRF = BigDecimal.ZERO;
        BigDecimal baseRestante = baseCalculo;

        for (int i = 0; i < IRPF_LIMITS.length && baseRestante.compareTo(BigDecimal.ZERO) > 0; i++) {
            BigDecimal limite = IRPF_LIMITS[i];
            BigDecimal limiteAnterior = i > 0 ? IRPF_LIMITS[i - 1] : BigDecimal.ZERO;
            
            if (baseCalculo.compareTo(limite) > 0) {
                BigDecimal valorTributavel = limite.subtract(limiteAnterior);
                BigDecimal imposto = valorTributavel.multiply(IRPF_RATES[i]);
                totalIRRF = totalIRRF.add(imposto);
                baseRestante = baseRestante.subtract(valorTributavel);
            } else {
                BigDecimal valorTributavel = baseCalculo.subtract(limiteAnterior);
                BigDecimal imposto = valorTributavel.multiply(IRPF_RATES[i]);
                totalIRRF = totalIRRF.add(imposto);
                break;
            }
        }

        // Se ainda sobrou valor, aplicar a alíquota máxima
        if (baseRestante.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal imposto = baseRestante.multiply(IRPF_RATES[IRPF_RATES.length - 1]);
            totalIRRF = totalIRRF.add(imposto);
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
