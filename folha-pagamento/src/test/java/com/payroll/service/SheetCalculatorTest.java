package com.payroll.service;

import com.payroll.entity.Employee;
import com.payroll.model.Employee.GrauInsalubridade; // Adicionar o import necessário
import org.junit.jupiter.api.Assertions; // Manter ou adicionar para asserções complexas
import java.math.BigDecimal;
import java.math.RoundingMode; // Adicionar para precisão
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class SheetCalculatorTest {

    private Employee employee;
    private SheetCalculator sheetCalculator; // Adicionar instância para métodos auxiliares

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
        
        // Instanciar o SheetCalculator (que contém os métodos auxiliares)
        sheetCalculator = new SheetCalculator();
    }

    /* * TESTES DE INSS e IRRF REMOVIDOS
     * Os testes para INSS e IRRF estão agora nas classes INSSTest e IRPFTest (Padrão Strategy).
     * Os métodos estáticos calcularINSS/IRRF foram removidos do SheetCalculator.
     */
     
    @Test
    @DisplayName("calcularSalarioHora_deveRetornarValorCorreto")
    void calcularSalarioHora_deveRetornarValorCorreto() {
        // Salário: 3000.00 / (40 horas * 4.33 semanas) = 17.32 (Aproximadamente)
        BigDecimal salarioHora = sheetCalculator.calcularSalarioHora(employee.getSalary(), employee.getWeeklyHours());
        
        // Usando o valor esperado baseado no cálculo de 4.33 semanas/mês: 3000 / 173.2 = 17.32
        assertEquals(new BigDecimal("17.32"), salarioHora);
    }
    
    @Test
    @DisplayName("calcularAdicionalPericulosidade_deveAplicar30Porcento")
    void calcularAdicionalPericulosidade_deveAplicar30Porcento() {
        // 3000.00 * 0.30 = 900.00
        BigDecimal adicional = sheetCalculator.calcularAdicionalPericulosidade(employee.getSalary());
        assertEquals(new BigDecimal("900.00"), adicional);
    }
    
    @Test
    @DisplayName("calcularAdicionalInsalubridade_medio_deveAplicar20PorcentoSalarioMinimo")
    void calcularAdicionalInsalubridade_medio_deveAplicar20PorcentoSalarioMinimo() {
        // Assumindo SALARIO_MINIMO = 1412.00 e grau MEDIO = 20%
        BigDecimal salarioMinimo = new BigDecimal("1412.00");
        BigDecimal valorEsperado = salarioMinimo.multiply(new BigDecimal("0.20")).setScale(2, RoundingMode.HALF_UP); // 282.40
        
        BigDecimal adicional = sheetCalculator.calcularAdicionalInsalubridade(salarioMinimo, GrauInsalubridade.MEDIO);
        assertEquals(valorEsperado, adicional);
    }
}