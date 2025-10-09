package com.payroll.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee("João", "12345678900", "Analista", LocalDate.of(2020, 1, 1), BigDecimal.valueOf(3000));
    }

    // Testa se o método exibirDados retorna corretamente as informações do funcionário
    @Test
    void testExibirDados() {
        String dados = employee.exibirDados();
        assertTrue(dados.contains("João"));
        assertTrue(dados.contains("12345678900"));
        assertTrue(dados.contains("Analista"));
        assertTrue(dados.contains("3000"));
        assertTrue(dados.contains("2020-01-01"));
    }

    // Testa se tem direito à insalubridade quando o grau é diferente de NENHUM
    @Test
    void testTemDireitoInsalubridade() {
        assertFalse(employee.temDireitoInsalubridade());
        employee.setGrauInsalubridade(Employee.GrauInsalubridade.MEDIO);
        assertTrue(employee.temDireitoInsalubridade());
    }

    // Testa cálculo de tempo de empresa em anos
    @Test
    void testCalcularTempo() {
        int anos = employee.calcularTempo();
        assertTrue(anos >= 3); // considerando que a data de admissão foi 2020-01-01
    }

    // Testa adicionar e listar benefícios
    @Test
    void testAdicionarEListarBeneficios() {
        employee.adicionarBeneficio("Vale Transporte");
        employee.adicionarBeneficio("Plano de Saúde");
        List<String> beneficios = employee.listarBeneficios();
        assertEquals(2, beneficios.size());
        assertTrue(beneficios.contains("Vale Transporte"));
        assertTrue(beneficios.contains("Plano de Saúde"));
    }
}