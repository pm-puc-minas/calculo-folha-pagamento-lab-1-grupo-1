package com.payroll.service;

/*
 * Serviço principal de processamento da Folha de Pagamento (Core Business Logic).
 * Orquestra todo o fluxo de cálculo, desde a recuperação dos dados do funcionário,
 * aplicação de regras trabalhistas (CLT), cálculo de impostos via estratégias (Strategy Pattern)
 * e persistência final do holerite gerado.
 */

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payroll.entity.PayrollCalculation;
import com.payroll.entity.Employee;
import com.payroll.repository.PayrollCalculationRepository;
import com.payroll.repository.EmployeeRepository;
import com.payroll.collections.CollectionOps;
import com.payroll.collections.GroupBySpec;
import com.payroll.exception.DataIntegrityBusinessException;
import com.payroll.exception.DatabaseConnectionException;
import com.payroll.exception.InputValidationException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import com.payroll.service.discount.DiscountCalculationContext;
import com.payroll.service.discount.DiscountStrategy;
import com.payroll.service.discount.DiscountType;

@Service
public class PayrollService implements IPayrollService {

    @Autowired
    private PayrollCalculationRepository payrollRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ReportsService reportsService;

    // Injeção de todas as estratégias de desconto disponíveis no contexto do Spring
    @Autowired
    private List<DiscountStrategy> discountStrategies;

    // Mapa para acesso rápido às estratégias por tipo (cache local)
    private Map<DiscountType, DiscountStrategy> discountStrategyMap;

    @Override
    public PayrollCalculation calculatePayroll(Long employeeId, String referenceMonth, Long calculatedBy) {
        try {
            // Verificar idempotência: se já existe folha para este mês, retorna a existente
            Optional<PayrollCalculation> existing = payrollRepository.findByEmployeeIdAndReferenceMonth(employeeId, referenceMonth);
            if (existing.isPresent()) return existing.get();
        } catch (DataAccessResourceFailureException e) {
            throw new DatabaseConnectionException("Falha de conexao ao verificar folha existente", e);
        }

        PayrollCalculation calculation = new PayrollCalculation();
        calculation.setReferenceMonth(referenceMonth);
        calculation.setCreatedBy(calculatedBy);

        // Vincula o empregado à folha
        Employee employee;
        try {
            employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + employeeId));
        } catch (DataAccessResourceFailureException e) {
            throw new DatabaseConnectionException("Falha de conexao ao buscar empregado", e);
        }
        calculation.setEmployee(employee);

        // --- Passo 1: Definição de Bases e Proventos ---
        BigDecimal baseSalary = nz(employee.getSalary());
        int weeklyHours = employee.getWeeklyHours() != null ? employee.getWeeklyHours() : 40;
        int workDaysMonth = 22;
        int dependents = employee.getDependents() != null ? employee.getDependents() : 0;

        BigDecimal hourlyWage = calcularSalarioHora(baseSalary, weeklyHours);
        
        // Cálculo de Adicional de Periculosidade (geralmente 30%)
        BigDecimal dangerousBonus = employee.getDangerousWork() != null && employee.getDangerousWork()
                ? baseSalary.multiply(employee.getDangerousPercentage() != null ? employee.getDangerousPercentage() : new BigDecimal("0.30"))
                : BigDecimal.ZERO;

        // Cálculo de Adicional de Insalubridade (baseado no nível de exposição)
        BigDecimal unhealthyBonus = BigDecimal.ZERO;
        String level = employee.getUnhealthyLevel() != null ? employee.getUnhealthyLevel().toUpperCase() : "NONE";
        switch (level) {
            case "LOW", "BAIXO" -> unhealthyBonus = baseSalary.multiply(new BigDecimal("0.10"));
            case "MEDIUM", "MEDIO", "MÉDIO" -> unhealthyBonus = baseSalary.multiply(new BigDecimal("0.20"));
            case "HIGH", "ALTO" -> unhealthyBonus = baseSalary.multiply(new BigDecimal("0.40"));
            default -> unhealthyBonus = BigDecimal.ZERO;
        }

        BigDecimal mealVoucher = employee.getMealVoucherValue() != null ? employee.getMealVoucherValue() : BigDecimal.ZERO;

        // Cálculo de Horas Extras (Adicional de 50%)
        BigDecimal overtimeHours = employee.getOvertimeHours() != null ? employee.getOvertimeHours() : BigDecimal.ZERO;
        BigDecimal overtimeValue = BigDecimal.ZERO;
        if (Boolean.TRUE.equals(employee.getOvertimeEligible()) && overtimeHours.compareTo(BigDecimal.ZERO) > 0) {
             overtimeValue = hourlyWage.multiply(new BigDecimal("1.5")).multiply(overtimeHours).setScale(2, RoundingMode.HALF_UP);
        }
        calculation.setOvertimeValue(overtimeValue);

        // Totalizar Salário Bruto
        BigDecimal grossSalary = baseSalary.add(dangerousBonus).add(unhealthyBonus).add(mealVoucher).add(overtimeValue);

        // --- Passo 2: Contexto para Cálculo de Descontos ---
        DiscountCalculationContext ctx = new DiscountCalculationContext()
                .setGrossSalary(grossSalary)
                .setDependents(dependents)
                .setTransportEnabled(Boolean.TRUE.equals(employee.getTransportVoucher()))
                .setPensionAlimony(BigDecimal.ZERO);

        // Configurar Valor do Vale Transporte (Valor fixo ou percentual legal)
        BigDecimal transportValue = nz(employee.getTransportVoucherValue());
        if (transportValue.compareTo(BigDecimal.ZERO) <= 0) {
            transportValue = grossSalary.multiply(PayrollConstants.TRANSPORTE_RATE);
        }
        ctx.setTransportVoucherValue(transportValue);

        // --- Passo 3: Execução das Estratégias de Desconto (Chain of Responsibility/Strategy) ---
        BigDecimal inssDiscount = strategy(DiscountType.INSS).calculate(ctx);
        ctx.setInssDiscount(inssDiscount); // Atualizar contexto pois o IRRF depende do INSS
        
        BigDecimal irrfDiscount = strategy(DiscountType.IRRF).calculate(ctx);
        BigDecimal transportDiscount = strategy(DiscountType.TRANSPORT).calculate(ctx);

        // Preenchimento dos resultados na entidade
        calculation.setHourlyWage(hourlyWage);
        calculation.setDangerousBonus(dangerousBonus.setScale(2, RoundingMode.HALF_UP));
        calculation.setUnhealthyBonus(unhealthyBonus.setScale(2, RoundingMode.HALF_UP));
        calculation.setGrossSalary(grossSalary.setScale(2, RoundingMode.HALF_UP));
        calculation.setInssDiscount(inssDiscount.setScale(2, RoundingMode.HALF_UP));
        calculation.setIrpfDiscount(irrfDiscount.setScale(2, RoundingMode.HALF_UP));
        calculation.setTransportDiscount(transportDiscount.setScale(2, RoundingMode.HALF_UP));

        BigDecimal fgts = calcularFGTS(grossSalary);
        calculation.setFgtsValue(fgts.setScale(2, RoundingMode.HALF_UP));

        calculation.setMealVoucherValue(mealVoucher.setScale(2, RoundingMode.HALF_UP));

        // --- Passo 4: Descontos de Benefícios Complementares (Planos) ---
        BigDecimal healthPlanDiscount = BigDecimal.ZERO;
        if (Boolean.TRUE.equals(employee.getHealthPlan())) {
            healthPlanDiscount = nz(employee.getHealthPlanValue());
        }
        calculation.setHealthPlanDiscount(healthPlanDiscount);

        BigDecimal dentalPlanDiscount = BigDecimal.ZERO;
        if (Boolean.TRUE.equals(employee.getDentalPlan())) {
            dentalPlanDiscount = nz(employee.getDentalPlanValue());
        }
        calculation.setDentalPlanDiscount(dentalPlanDiscount);

        BigDecimal gymDiscount = BigDecimal.ZERO;
        if (Boolean.TRUE.equals(employee.getGym())) {
            gymDiscount = nz(employee.getGymValue());
        }
        calculation.setGymDiscount(gymDiscount);

        // Totalizar Descontos
        BigDecimal totalDiscounts = inssDiscount.add(irrfDiscount).add(fgts).add(transportDiscount)
                                            .add(healthPlanDiscount).add(dentalPlanDiscount).add(gymDiscount);

        // Validações Finais de Integridade Financeira
        if (grossSalary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InputValidationException("Salario bruto deve ser maior que zero",
                Map.of("grossSalary", grossSalary));
        }
        if (totalDiscounts.compareTo(grossSalary) >= 0) {
            throw new InputValidationException("Descontos nao podem ser maiores ou iguais ao salario bruto",
                Map.of("grossSalary", grossSalary, "totalDiscounts", totalDiscounts));
        }

        BigDecimal netSalary = grossSalary.subtract(totalDiscounts);
        calculation.setNetSalary(netSalary.setScale(2, RoundingMode.HALF_UP));

        // --- Passo 5: Persistência e Auditoria ---
        try {
            PayrollCalculation saved = payrollRepository.save(calculation);
            
            // Gerar relatório PDF automaticamente
            try {
                reportsService.createReport(employeeId, referenceMonth, "PAYROLL", calculatedBy);
            } catch (Exception e) {
                // Logar falha no relatório sem abortar a transação principal
                e.printStackTrace();
            }
            
            return saved;
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityBusinessException("Violacao de integridade ao salvar calculo de folha", e);
        } catch (DataAccessResourceFailureException e) {
            throw new DatabaseConnectionException("Falha de conexao ao salvar calculo de folha", e);
        }
    }

    private BigDecimal nz(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }

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
    public BigDecimal calcularAdicionalInsalubridade(BigDecimal salarioMinimo, com.payroll.model.Employee.GrauInsalubridade grau) {
        if (salarioMinimo == null || grau == null || grau == com.payroll.model.Employee.GrauInsalubridade.NENHUM) return BigDecimal.ZERO;

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
        DiscountCalculationContext ctx = new DiscountCalculationContext()
                .setGrossSalary(salarioBruto)
                .setTransportVoucherValue(valorEntregue)
                .setTransportEnabled(valorEntregue != null && valorEntregue.compareTo(BigDecimal.ZERO) > 0);
        return strategy(DiscountType.TRANSPORT).calculate(ctx);
    }

    @Override
    public BigDecimal calcularValeAlimentacao(BigDecimal valorDiario, int diasTrabalhados) {
        if (valorDiario == null || diasTrabalhados <= 0) return BigDecimal.ZERO;
        return valorDiario.multiply(new BigDecimal(diasTrabalhados)).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularINSS(BigDecimal salarioContribuicao) {
        DiscountCalculationContext ctx = new DiscountCalculationContext().setGrossSalary(salarioContribuicao);
        return strategy(DiscountType.INSS).calculate(ctx);
    }

    @Override
    public BigDecimal calcularFGTS(BigDecimal baseCalculoFGTS) {
        if (baseCalculoFGTS == null || baseCalculoFGTS.compareTo(BigDecimal.ZERO) <= 0)
            return BigDecimal.ZERO;
        return baseCalculoFGTS.multiply(PayrollConstants.FGTS_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularIRRF(BigDecimal salarioBruto, BigDecimal descontoINSS, int numDependentes, BigDecimal pensaoAlimenticia) {
        DiscountCalculationContext ctx = new DiscountCalculationContext()
                .setGrossSalary(salarioBruto)
                .setInssDiscount(descontoINSS)
                .setDependents(numDependentes)
                .setPensionAlimony(pensaoAlimenticia);
        return strategy(DiscountType.IRRF).calculate(ctx);
    }

    @Override
    public List<PayrollCalculation> getEmployeePayrolls(Long employeeId) {
        List<PayrollCalculation> list;
        try {
            list = payrollRepository.findByEmployeeId(employeeId);
        } catch (DataAccessResourceFailureException e) {
            throw new DatabaseConnectionException("Falha de conexao ao listar folhas do empregado", e);
        }
        return CollectionOps.filter(list, pc -> pc != null && pc.getEmployee() != null && Objects.equals(pc.getEmployee().getId(), employeeId));
    }

    @Override
    public List<PayrollCalculation> getAllPayrolls() {
        List<PayrollCalculation> all;
        try {
            all = payrollRepository.findAll();
        } catch (DataAccessResourceFailureException e) {
            throw new DatabaseConnectionException("Falha de conexao ao listar todas as folhas", e);
        }
        return all.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    // Filtros de Relatórios e Auditoria
    
    public List<PayrollCalculation> filterPayrollsByNetSalaryRange(BigDecimal min, BigDecimal max) {
        List<PayrollCalculation> all = getAllPayrolls();
        return CollectionOps.filter(all, pc -> {
            BigDecimal net = pc.getNetSalary();
            if (net == null) return false;
            boolean geMin = (min == null) || net.compareTo(min) >= 0;
            boolean leMax = (max == null) || net.compareTo(max) <= 0;
            return geMin && leMax;
        });
    }

    public Map<String, List<PayrollCalculation>> groupPayrollsByMonth() {
        List<PayrollCalculation> all = getAllPayrolls();
        return CollectionOps.groupBy(all, new GroupBySpec<String, PayrollCalculation>() {
            @Override
            public String key(PayrollCalculation item) {
                return item.getReferenceMonth();
            }
        });
    }

    // Busca de casos extremos (Salário Negativo ou Desconto Total) para auditoria
    public List<PayrollCalculation> findEdgeCasePayrolls() {
        List<PayrollCalculation> all = getAllPayrolls();
        return CollectionOps.filter(all, pc -> {
            BigDecimal gross = pc.getGrossSalary();
            BigDecimal inss = pc.getInssDiscount();
            BigDecimal irpf = pc.getIrpfDiscount();
            BigDecimal transport = pc.getTransportDiscount();
            BigDecimal fgts = pc.getFgtsValue();
            BigDecimal totalDiscounts = CollectionOps.sum(List.of(inss, irpf, transport, fgts), v -> v);
            boolean nonPositiveGross = gross == null || gross.compareTo(BigDecimal.ZERO) <= 0;
            boolean fullDiscount = (gross != null) && totalDiscounts.compareTo(gross) >= 0;
            return nonPositiveGross || fullDiscount;
        });
    }

    // Soma total de descontos aplicados a um funcionário em todo o histórico
    public BigDecimal totalDiscountsForEmployee(Long employeeId) {
        List<PayrollCalculation> list = getEmployeePayrolls(employeeId);
        return CollectionOps.sum(list, pc -> {
            BigDecimal inss = pc.getInssDiscount();
            BigDecimal irpf = pc.getIrpfDiscount();
            BigDecimal transport = pc.getTransportDiscount();
            BigDecimal fgts = pc.getFgtsValue();
            BigDecimal total = BigDecimal.ZERO;
            if (inss != null) total = total.add(inss);
            if (irpf != null) total = total.add(irpf);
            if (transport != null) total = total.add(transport);
            if (fgts != null) total = total.add(fgts);
            return total;
        });
    }

    // Helper para selecionar a estratégia correta no mapa
    private DiscountStrategy strategy(DiscountType type) {
        if (discountStrategyMap == null) {
            discountStrategyMap = discountStrategies.stream()
                    .collect(Collectors.toMap(DiscountStrategy::getType, s -> s));
        }
        return discountStrategyMap.get(type);
    }
}