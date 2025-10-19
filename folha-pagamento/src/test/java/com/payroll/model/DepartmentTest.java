package com.payroll.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DepartmentTest {

    private Department department;
    private Employee emp1;
    private Employee emp2;

    @BeforeEach
    void setUp() {
        department = new Department(1, "Finance");
        emp1 = new Employee();
        emp2 = new Employee();
        // Não usamos setFullName, pois o Employee atual não tem esse método
    }

    // Testa adicionar um funcionário ao departamento
    @Test
    @DisplayName("Adiciona funcionário ao departamento")
    // Verifica adição de funcionário ao departamento
    void deveAdicionarFuncionarioAoDepartamento() {
        department.adicionarFuncionario(emp1);
        List<Employee> employees = department.listarFuncionarios();
        assertEquals(1, employees.size());
        assertTrue(employees.contains(emp1));
    }

    // Testa remover um funcionário do departamento
    @Test
    @DisplayName("Remove funcionário do departamento")
    // Verifica remoção de funcionário do departamento
    void deveRemoverFuncionarioDoDepartamento() {
        department.adicionarFuncionario(emp1);
        department.adicionarFuncionario(emp2);
        department.removerFuncionario(emp1);
        List<Employee> employees = department.listarFuncionarios();
        assertEquals(1, employees.size());
        assertTrue(employees.contains(emp2));
        assertFalse(employees.contains(emp1));
    }

    // Testa listar funcionários retorna a lista correta
    @Test
    @DisplayName("Lista funcionários do departamento")
    // Verifica listagem de funcionários do departamento
    void deveListarFuncionariosDoDepartamento() {
        department.adicionarFuncionario(emp1);
        department.adicionarFuncionario(emp2);
        List<Employee> employees = department.listarFuncionarios();
        assertEquals(2, employees.size());
        assertTrue(employees.contains(emp1));
        assertTrue(employees.contains(emp2));
    }

    // Testa exibir informações do departamento
    @Test
    @DisplayName("Exibe informações do departamento")
    // Verifica exibição das informações do departamento
    void deveExibirInformacoesDoDepartamento() {
        department.adicionarFuncionario(emp1);
        String info = department.exibirInformacoes();
        assertEquals("Department: Finance (ID: 1) - 1 funcionários", info);
    }
}