package com.payroll.repository;

import com.payroll.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para o EmployeeRepository.
 */
@DataJpaTest
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll(); // garante que cada teste comece com o repositório vazio

        employee = new Employee();
        employee.setFullName("João da Silva");
        employee.setCpf("12345678900");
        employee.setRg("MG1234567");
        employee.setPosition("Analista");
        employee.setAdmissionDate(LocalDate.of(2023, 1, 1));
        employee.setSalary(BigDecimal.valueOf(3000));
        employee.setWeeklyHours(40);

        employeeRepository.save(employee);
    }

    @Test
    @DisplayName("Deve encontrar funcionário pelo CPF existente")
    // Busca funcionário válido pelo CPF
    void deveEncontrarFuncionarioPorCpf() {
        assertTrue(employeeRepository.findByCpf("12345678900").isPresent(),
                "O funcionário com CPF 12345678900 deve ser encontrado");
    }

    @Test
    @DisplayName("Deve verificar existência de funcionário pelo CPF")
    // Verifica existência por CPF
    void deveVerificarExistenciaPorCpf() {
        assertTrue(employeeRepository.existsByCpf("12345678900"),
                "O funcionário com CPF 12345678900 deve existir");
    }

    @Test
    @DisplayName("Deve retornar vazio quando CPF não existe")
    // Garante vazio quando CPF não cadastrado
    void deveRetornarVazioQuandoCpfNaoExiste() {
        assertFalse(employeeRepository.findByCpf("00000000000").isPresent(),
                "Não deve encontrar funcionário com CPF inexistente");
    }
}

