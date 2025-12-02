package com.payroll.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Comparator; // NOVO: Import para ordenação
import com.payroll.service.SheetCalculator.DescontoContext; // NOVO: Importa a classe de Contexto

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payroll.entity.PayrollCalculation;
import com.payroll.entity.Employee;
import com.payroll.model.Employee.GrauInsalubridade;
import com.payroll.repository.PayrollCalculationRepository;
import com.payroll.repository.EmployeeRepository;
import com.payroll.collections.CollectionOps;
import com.payroll.collections.FilterSpec;
import com.payroll.collections.GroupBySpec;
import com.payroll.exception.DataIntegrityBusinessException;
import com.payroll.exception.DatabaseConnectionException;
import com.payroll.exception.InputValidationException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;

@Service
public class PayrollService implements IPayrollService {

    // Mantido: Injeção de repositórios
    private PayrollCalculationRepository payrollRepository;
    private EmployeeRepository employeeRepository;

    // NOVO: Campo para armazenar a lista de estratégias (o Contexto do Padrão Strategy)
    private final List<IDesconto> calculoDescontos; 

    /**
     * NOVO: Construtor que recebe todas as implementações de IDesconto (Estratégias)
     * e as ordena usando a prioridade.
     */
    public PayrollService(List<IDesconto> calculoDescontos) {
        // NOVO: Ordena as estratégias pela prioridade para garantir a ordem de execução (INSS -> IRPF)
        this.calculoDescontos = calculoDescontos.stream()
            .sorted(Comparator.comparing(IDesconto::prioridade))
            .collect(Collectors.toList());
    }

    // NOVO: Setter para repositórios (Alternativa para injeção de campo quando há construtor customizado)
    @Autowired
    public void setRepositories(PayrollCalculationRepository payrollRepository, EmployeeRepository employeeRepository) {
        this.payrollRepository = payrollRepository;
        this.employeeRepository = employeeRepository;
    }


    @Override
    public PayrollCalculation calculatePayroll(Long employeeId, String referenceMonth, Long calculatedBy) {
        try {
            Optional<PayrollCalculation> existing = payrollRepository.findByEmployeeIdAndReferenceMonth(employeeId, referenceMonth);
            if (existing.isPresent()) return existing.get();
        } catch (DataAccessResourceFailureException e) {
            throw new DatabaseConnectionException("Falha de conexão ao verificar folha existente", e);
        }

        PayrollCalculation calculation = new PayrollCalculation();
        calculation.setReferenceMonth(referenceMonth);
        calculation.setCreatedBy(calculatedBy);

        // Vincula o empregado à folha (necessário para validação @NotNull)
        Employee employee;
        try {
            employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + employeeId));
        } catch (DataAccessResourceFailureException e) {
            throw new DatabaseConnectionException("Falha de conexão ao buscar empregado", e);
        }
        calculation.setEmployee(employee);

        // Simulação de dados de funcionário
        BigDecimal baseSalary = new BigDecimal("3000.00");
        
        // Mantido: Busca de dados necessários para o Contexto
        int dependents = 0;
        BigDecimal pensionAlimony = BigDecimal.ZERO;
        BigDecimal transportVoucher = new BigDecimal("150.00"); // Apenas para o desconto de VT

        BigDecimal hourlyWage = calcularSalarioHora(baseSalary, 40);
        BigDecimal dangerousBonus = calcularAdicionalPericulosidade(baseSalary);
        BigDecimal unhealthyBonus = calcularAdicionalInsalubridade(PayrollConstants.SALARIO_MINIMO, GrauInsalubridade.MEDIO);

        calculation.setHourlyWage(hourlyWage);
        calculation.setDangerousBonus(dangerousBonus);
        calculation.setUnhealthyBonus(unhealthyBonus);

        BigDecimal grossSalary = baseSalary.add(dangerousBonus).add(unhealthyBonus);
        calculation.setGrossSalary(grossSalary);
        
        // ====================================================================================
        // NOVO: ORQUESTRAÇÃO DOS DESCONTOS (Padrão Strategy)
        // ====================================================================================
        
        // 1. Cria o Contexto
        DescontoContext context = new DescontoContext(grossSalary, dependents, pensionAlimony); 
        
        BigDecimal totalMandatoryDiscounts = BigDecimal.ZERO;

        // 2. Itera sobre as estratégias (INSS, IRPF, etc.) na ordem de Prioridade (garantida pelo construtor)
        for (IDesconto estrategia : calculoDescontos) {
            BigDecimal desconto = estrategia.calcular(context);
            totalMandatoryDiscounts = totalMandatoryDiscounts.add(desconto);
            
            // NOVO: Salva os resultados das principais estratégias na entidade PayrollCalculation
            if (estrategia instanceof INSS) {
                calculation.setInssDiscount(desconto);
            } else if (estrategia instanceof IRPF) {
                calculation.setIrpfDiscount(desconto);
            }
        }
        
        // Linhas removidas que chamavam o calcularINSS/calcularIRRF antigo.
        
        // --- CÁLCULOS REMANESCENTES / BENEFÍCIOS ---
        
        BigDecimal transportDiscount = calcularDescontoValeTransporte(grossSalary, transportVoucher);
        
        calculation.setTransportDiscount(transportDiscount);

        BigDecimal fgts = calcularFGTS(grossSalary);
        calculation.setFgtsValue(fgts);

        BigDecimal mealVoucher = calcularValeAlimentacao(new BigDecimal("25.00"), 22);
        calculation.setMealVoucherValue(mealVoucher);

        // líquido = bruto − (Mandatórios + VT + FGTS)
        BigDecimal totalDiscounts = totalMandatoryDiscounts.add(transportDiscount).add(fgts);
        
        // Mantido: Validações
        if (grossSalary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InputValidationException("Salário bruto deve ser maior que zero",
                Map.of("grossSalary", grossSalary));
        }
        if (totalDiscounts.compareTo(grossSalary) >= 0) {
            throw new InputValidationException("Descontos não podem ser maiores ou iguais ao salário bruto",
                Map.of("grossSalary", grossSalary, "totalDiscounts", totalDiscounts));
        }

        BigDecimal netSalary = grossSalary.subtract(totalDiscounts);
        calculation.setNetSalary(netSalary);

        // Mantido: Salvamento
        try {
            return payrollRepository.save(calculation);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityBusinessException("Violação de integridade ao salvar cálculo de folha", e);
        } catch (DataAccessResourceFailureException e) {
            throw new DatabaseConnectionException("Falha de conexão ao salvar cálculo de folha", e);
        }
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
    public BigDecimal calcularFGTS(BigDecimal baseCalculoFGTS) {
        if (baseCalculoFGTS == null || baseCalculoFGTS.compareTo(BigDecimal.ZERO) <= 0)
            return BigDecimal.ZERO;
        return baseCalculoFGTS.multiply(PayrollConstants.FGTS_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public List<PayrollCalculation> getEmployeePayrolls(Long employeeId) {
        List<PayrollCalculation> list;
        try {
            list = payrollRepository.findByEmployeeId(employeeId);
        } catch (DataAccessResourceFailureException e) {
            throw new DatabaseConnectionException("Falha de conexão ao listar folhas do empregado", e);
        }
        return CollectionOps.filter(list, pc -> pc != null && pc.getEmployee() != null && Objects.equals(pc.getEmployee().getId(), employeeId));
    }

    @Override
    public List<PayrollCalculation> getAllPayrolls() {
        List<PayrollCalculation> all;
        try {
            all = payrollRepository.findAll();
        } catch (DataAccessResourceFailureException e) {
            throw new DatabaseConnectionException("Falha de conexão ao listar todas as folhas", e);
        }
        return all.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

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
}