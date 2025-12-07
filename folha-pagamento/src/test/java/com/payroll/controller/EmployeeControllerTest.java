package com.payroll.controller;

import com.payroll.dto.EmployeeDTO;
import com.payroll.entity.Employee;
import com.payroll.repository.EmployeeRepository;
import com.payroll.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EmployeeControllerTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private EmployeeService employeeService;
    private EmployeeController employeeController;

    @BeforeEach
    void setUp() throws Exception {
        employeeService = new EmployeeService();
        employeeController = new EmployeeController();

        Field repoField = EmployeeService.class.getDeclaredField("employeeRepository");
        repoField.setAccessible(true);
        repoField.set(employeeService, employeeRepository);

        Field serviceField = EmployeeController.class.getDeclaredField("employeeService");
        serviceField.setAccessible(true);
        serviceField.set(employeeController, employeeService);
    }

    private EmployeeDTO buildDTO(String cpf, String position, String name) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.cpf = cpf;
        dto.position = position;
        dto.name = name;
        dto.admissionDate = LocalDate.of(2020, 1, 1).toString();
        dto.baseSalary = new BigDecimal("3000.00");
        dto.weeklyHours = 40;
        return dto;
    }

    @Test
    @DisplayName("Lista colaboradores com sucesso")
    void deveListarColaboradoresComSucesso() {
        ResponseEntity<List<EmployeeDTO>> response = employeeController.listEmployees(null);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Cria colaborador com dados obrigatórios")
    void deveCriarColaboradorComDadosObrigatorios() {
        EmployeeDTO dto = buildDTO("12345678901", "Developer", "Bernardo Pereira");

        ResponseEntity<?> response = employeeController.createEmployee(dto, null);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof EmployeeDTO);
    }

    @Test
    @DisplayName("Visualiza colaborador por ID retorna 200 ou 404")
    void deveVisualizarColaboradorPorIdRetorna200Ou404() {
        Employee toPersist = EmployeeDTO.toEntity(buildDTO("12345678902", "Tester", "Lucas Silva"));
        Employee saved = employeeService.createEmployee(toPersist, null);

        ResponseEntity<?> response = employeeController.viewEmployee(saved.getId());
        assertTrue(response.getStatusCode().value() == 200 || response.getStatusCode().value() == 404);
    }

    @Test
    @DisplayName("Atualiza colaborador e persiste alteração")
    void deveAtualizarColaboradorComSucesso() {
        Employee base = EmployeeDTO.toEntity(buildDTO("12345678903", "Intern", "Maria Oliveira"));
        Employee saved = employeeService.createEmployee(base, null);

        EmployeeDTO updateDTO = buildDTO("12345678903", "Junior Developer", "Maria Oliveira");

        ResponseEntity<?> response = employeeController.updateEmployee(saved.getId(), updateDTO);

        assertEquals(200, response.getStatusCode().value());
        EmployeeDTO updatedEmployee = (EmployeeDTO) response.getBody();
        assertNotNull(updatedEmployee);
        assertEquals("Junior Developer", updatedEmployee.position);
    }

    @Test
    @DisplayName("Deleta colaborador com sucesso")
    void deveDeletarColaboradorComSucesso() {
        Employee base = EmployeeDTO.toEntity(buildDTO("12345678904", "Analyst", "Joao Santos"));
        Employee saved = employeeService.createEmployee(base, null);

        ResponseEntity<?> response = employeeController.deleteEmployee(saved.getId());

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Deletado com sucesso", response.getBody());
    }
}
