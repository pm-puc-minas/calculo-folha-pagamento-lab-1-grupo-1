package com.payroll.service;

import com.payroll.entity.PayrollCalculation;
import com.payroll.repository.PayrollCalculationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class PayrollServiceTest {

    private PayrollService payrollService;
    private FakePayrollCalculationRepository fakePayrollRepository;

    // Fake repository simples para PayrollCalculation
    static class FakePayrollCalculationRepository implements PayrollCalculationRepository {

        private Map<Long, PayrollCalculation> storage = new HashMap<>();
        private AtomicLong idGenerator = new AtomicLong(1);

        @Override
        public PayrollCalculation save(PayrollCalculation payrollCalculation) {
            if (payrollCalculation.getId() == null) {
                payrollCalculation.setId(idGenerator.getAndIncrement());
            }
            storage.put(payrollCalculation.getId(), payrollCalculation);
            return payrollCalculation;
        }

        @Override
        public Optional<PayrollCalculation> findByEmployeeIdAndReferenceMonth(Long employeeId, String referenceMonth) {
            return storage.values().stream()
                    .filter(p -> p.getReferenceMonth().equals(referenceMonth) && Objects.equals(p.getEmployeeId(), employeeId))
                    .findFirst();
        }

        @Override
        public List<PayrollCalculation> findByEmployeeId(Long employeeId) {
            List<PayrollCalculation> result = new ArrayList<>();
            for (PayrollCalculation p : storage.values()) {
                if (Objects.equals(p.getEmployeeId(), employeeId)) {
                    result.add(p);
                }
            }
            return result;
        }

        @Override
        public List<PayrollCalculation> findAll() {
            return new ArrayList<>(storage.values());
        }

        @Override
        public void deleteById(Long id) {
            storage.remove(id);
        }
    }

    @BeforeEach
    void setUp() {
        fakePayrollRepository = new FakePayrollCalculationRepository();
        payrollService = new PayrollService();
        payrollService.payrollRepository = fakePayrollRepository; // injetar fake repo
    }

    @Test
    void testCalculatePayroll_newCalculation() {
        Long employeeId = 1L;
        String month = "2024-10";
        Long calculatedBy = 99L;

        PayrollCalculation calc = payrollService.calculatePayroll(employeeId, month, calculatedBy);

        assertNotNull(calc.getId());
        assertEquals(month, calc.getReferenceMonth());
        assertEquals(calculatedBy, calc.getCreatedBy());
        assertEquals(employeeId, calc.getEmployeeId());

        // Validar cálculo salário hora (salário fixo 3000 / (40*4.33))
        BigDecimal expectedHourlyWage = new BigDecimal("3000.00")
                .divide(new BigDecimal(40 * 4.33), 2, BigDecimal.ROUND_HALF_UP);
        assertEquals(expectedHourlyWage, calc.getHourlyWage());

        // Validar bônus perigosidade (30% do salário base 3000)
        BigDecimal expectedDangerousBonus = new BigDecimal("3000.00")
                .multiply(new BigDecimal("0.30")).setScale(2, BigDecimal.ROUND_HALF_UP);
        assertEquals(expectedDangerousBonus, calc.getDangerousBonus());

        // Validar bônus insalubridade (20% do salário base 3000, pois nível "MEDIO")
        BigDecimal expectedUnhealthyBonus = new BigDecimal("3000.00")
                .multiply(new BigDecimal("0.20")).setScale(2, BigDecimal.ROUND_HALF_UP);
        assertEquals(expectedUnhealthyBonus, calc.getUnhealthyBonus());

        // Salário bruto = base + perigosidade + insalubridade
        BigDecimal expectedGrossSalary = new BigDecimal("3000.00")
                .add(expectedDangerousBonus)
                .add(expectedUnhealthyBonus);
        assertEquals(expectedGrossSalary, calc.getGrossSalary());

        // INSS, IRPF, Transporte, FGTS - não testamos os valores exatos aqui, mas que estejam calculados
        assertNotNull(calc.getInssDiscount());
        assertNotNull(calc.getIrpfDiscount());
        assertNotNull(calc.getTransportDiscount());
        assertNotNull(calc.getFgtsValue());
        assertNotNull(calc.getMealVoucherValue());

        // Salário líquido = bruto - descontos
        BigDecimal totalDiscounts = calc.getInssDiscount()
                .add(calc.getIrpfDiscount())
                .add(calc.getTransportDiscount());
        assertEquals(calc.getNetSalary(), calc.getGrossSalary().subtract(totalDiscounts));
    }

    @Test
    void testCalculatePayroll_existingCalculation() {
        Long employeeId = 2L;
        String month = "2024-11";
        Long calculatedBy = 50L;

        // Criar cálculo pré-existente
        PayrollCalculation existing = new PayrollCalculation();
        existing.setEmployeeId(employeeId);
        existing.setReferenceMonth(month);
        existing.setCreatedBy(calculatedBy);
        existing.setId(1L);
        fakePayrollRepository.save(existing);

        PayrollCalculation result = payrollService.calculatePayroll(employeeId, month, calculatedBy);

        // Deve retornar cálculo existente, não criar novo
        assertEquals(existing.getId(), result.getId());
        assertEquals(existing.getReferenceMonth(), result.getReferenceMonth());
        assertEquals(existing.getCreatedBy(), result.getCreatedBy());
    }

    @Test
    void testGetEmployeePayrolls() {
        Long employeeId = 10L;

        PayrollCalculation p1 = new PayrollCalculation();
        p1.setEmployeeId(employeeId);
        p1.setReferenceMonth("2024-01");
        p1.setId(1L);
        fakePayrollRepository.save(p1);

        PayrollCalculation p2 = new PayrollCalculation();
        p2.setEmployeeId(employeeId);
        p2.setReferenceMonth("2024-02");
        p2.setId(2L);
        fakePayrollRepository.save(p2);

        PayrollCalculation p3 = new PayrollCalculation();
        p3.setEmployeeId(999L);
        p3.setReferenceMonth("2024-02");
        p3.setId(3L);
        fakePayrollRepository.save(p3);

        List<PayrollCalculation> list = payrollService.getEmployeePayrolls(employeeId);
        assertEquals(2, list.size());
        assertTrue(list.stream().allMatch(pc -> pc.getEmployeeId().equals(employeeId)));
    }

    @Test
    void testGetAllPayrolls() {
        PayrollCalculation p1 = new PayrollCalculation();
        p1.setId(1L);
        fakePayrollRepository.save(p1);

        PayrollCalculation p2 = new PayrollCalculation();
        p2.setId(2L);
        fakePayrollRepository.save(p2);

        List<PayrollCalculation> all = payrollService.getAllPayrolls();
        assertTrue(all.size() >= 2);
        assertTrue(all.stream().anyMatch(pc -> pc.getId().equals(1L)));
        assertTrue(all.stream().anyMatch(pc -> pc.getId().equals(2L)));
    }
}
