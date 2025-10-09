package com.payroll.model;

import org.junit.jupiter.api.BeforeEach;
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
    void testAdicionarFuncionario() {
        department.adicionarFuncionario(emp1);
        List<Employee> employees = department.listarFuncionarios();
        assertEquals(1, employees.size());
        assertTrue(employees.contains(emp1));
    }

    // Testa remover um funcionário do departamento
    @Test
    void testRemoverFuncionario() {
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
    void testListarFuncionarios() {
        department.adicionarFuncionario(emp1);
        department.adicionarFuncionario(emp2);
        List<Employee> employees = department.listarFuncionarios();
        assertEquals(2, employees.size());
        assertTrue(employees.contains(emp1));
        assertTrue(employees.contains(emp2));
    }

    // Testa exibir informações do departamento
    @Test
    void testExibirInformacoes() {
        department.adicionarFuncionario(emp1);
        String info = department.exibirInformacoes();
        assertEquals("Department: Finance (ID: 1) - 1 funcionários", info);
    }
}