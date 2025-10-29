package com.payroll.service;

import com.payroll.entity.Employee;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class SheetCalculatorTest {

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setFullName("Teste");
        employee.setCpf("00000000000");
        employee.setRg("RG");
        employee.setPosition("Dev");
        employee.setSalary(new BigDecimal("3000.00"));
        employee.setWeeklyHours(40);
        employee.setDependents(0);
    }

    

    @Test
    @DisplayName("calcularINSS_deveSerProgressivo_paraSalarioDoEmployee")
    // Calcula INSS progressivo usando o salário do Employee
    void calcularINSS_deveSerProgressivo_paraSalarioDoEmployee() {
        BigDecimal inss = SheetCalculator.calcularINSS(employee.getSalary());
        assertEquals(new BigDecimal("258.82"), inss);
    }

    @Test
    @DisplayName("calcularIRRF_deveUsarBaseDoEmployee_semPensao_eVariarPorDependentes")
    // Calcula IRRF (faixa única, sem parcela a deduzir) variando dependentes
    void calcularIRRF_deveUsarBaseDoEmployee_semPensao_eVariarPorDependentes() {
        BigDecimal salarioBruto = employee.getSalary();
        BigDecimal inss = SheetCalculator.calcularINSS(salarioBruto);

        employee.setDependents(0);
        BigDecimal irrf0 = SheetCalculator.calcularIRRF(salarioBruto, inss, employee.getDependents());
        assertEquals(new BigDecimal("205.59"), irrf0);

        employee.setDependents(2);
        BigDecimal irrf2 = SheetCalculator.calcularIRRF(salarioBruto, inss, employee.getDependents());
        assertEquals(new BigDecimal("177.15"), irrf2);

        employee.setDependents(3);
        BigDecimal irrf3 = SheetCalculator.calcularIRRF(salarioBruto, inss, employee.getDependents());
        // Usa compareTo para ignorar diferença de escala (0 vs 0.00)
        org.junit.jupiter.api.Assertions.assertEquals(0, irrf3.compareTo(BigDecimal.ZERO));
    }
}
