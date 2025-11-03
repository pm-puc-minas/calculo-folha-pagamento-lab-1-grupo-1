package com.payroll.controller;

import com.payroll.dtos.employee.EmployeeRequestDTO; // Importação para o DTO de entrada
import com.payroll.dtos.employee.EmployeeResponseDTO; // Importação para o DTO de saída
import com.payroll.entity.Employee;
import com.payroll.repository.EmployeeRepository;
import com.payroll.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de teste de integração para EmployeeController, utilizando o banco de dados H2 em memória.
 * Os testes garantem que o Controller e o Service interajam corretamente com o Repository.
 */
@DataJpaTest
class EmployeeControllerTest {

    @Autowired
    private EmployeeRepository employeeRepository; // Repository real (injetado pelo DataJpaTest)

    private EmployeeService employeeService;
    private EmployeeController employeeController;

    /**
     * Configura o ambiente de teste antes de cada método,
     * instanciando Service e Controller e injetando o Repository real via Reflection.
     */
    @BeforeEach
    void setUp() throws Exception {
        // Instancia Service e Controller
        employeeService = new EmployeeService();
        employeeController = new EmployeeController(); 

        // Injeção manual do repository no service (necessário em testes @DataJpaTest/unidade)
        Field repoField = EmployeeService.class.getDeclaredField("employeeRepository");
        repoField.setAccessible(true);
        repoField.set(employeeService, employeeRepository);

        // Injeção manual do service no controller
        Field serviceField = EmployeeController.class.getDeclaredField("employeeService");
        serviceField.setAccessible(true);
        serviceField.set(employeeController, employeeService);
        
        // Nota: Outras injeções (como UserService) seriam necessárias aqui se usadas nos endpoints.
    }

    @Test
    @DisplayName("Lista colaboradores com sucesso")
    // Valida retorno 200, corpo não nulo e o tipo de retorno List<EmployeeResponseDTO>
    void deveListarColaboradoresComSucesso() {
        ResponseEntity<?> response = employeeController.listEmployees();
        
        // Verifica o status HTTP
        assertEquals(200, response.getStatusCode().value());
        
        // Verifica se o corpo não é nulo
        assertNotNull(response.getBody());
        
        // Verifica se a serialização está correta (retorno é uma lista de DTOs)
        assertTrue(response.getBody() instanceof List, "O corpo deve ser uma lista.");
    }

    @Test
    @DisplayName("Cria colaborador com dados obrigatórios")
    // Valida criação de colaborador com campos mínimos obrigatórios, retornando 201 e um DTO de Resposta
    void deveCriarColaboradorComDadosObrigatorios() {
        // Cria o DTO de Requisição (simula o JSON de entrada)
        EmployeeRequestDTO requestDTO = new EmployeeRequestDTO(
            null,
            "Bernardo Pereira", 
            "12345678901", 
            "MG123456", 
            "Developer", 
            LocalDate.of(2020, 1, 1), 
            new BigDecimal("3000"),
            0,
            40,
            false, null, null, null, null, null, null
        );

        // Chama o método do Controller com o DTO
        ResponseEntity<?> response = employeeController.createEmployee(requestDTO, null);

        // Verifica o status de sucesso na criação
        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        
        // Verifica se a serialização está correta (retorno é um DTO de Resposta)
        assertTrue(response.getBody() instanceof EmployeeResponseDTO, "O corpo deve ser um DTO de Resposta.");
    }

    @Test
    @DisplayName("Visualiza colaborador por ID retorna 200 ou 404")
    // Valida que consultar colaborador por ID retorna sucesso (200) com um DTO ou 404 quando ausente
    void deveVisualizarColaboradorPorIdRetorna200Ou404() {
        // 1. Cria e salva um novo funcionário via Controller para obter o ID
        EmployeeRequestDTO requestDTO = new EmployeeRequestDTO(
            null, 
            "Lucas Silva", 
            "12345678902", 
            "MG654321", 
            "Tester", 
            LocalDate.of(2021, 5, 10), 
            new BigDecimal("2500"),
            0,
            40,
            false, null, null, null, null, null, null
        );
        ResponseEntity<?> createResponse = employeeController.createEmployee(requestDTO, null);
        EmployeeResponseDTO savedDTO = (EmployeeResponseDTO) createResponse.getBody();

        // 2. Visualiza o funcionário recém-criado
        ResponseEntity<?> response = employeeController.viewEmployee(savedDTO.getId());

        // Verifica o status de sucesso e o tipo de retorno
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody() instanceof EmployeeResponseDTO, "A visualização deve retornar um DTO de Resposta.");
        
        // Testa o cenário de falha (ID inexistente)
        ResponseEntity<?> notFoundResponse = employeeController.viewEmployee(-1L);
        assertEquals(404, notFoundResponse.getStatusCode().value());
    }

    @Test
    @DisplayName("Atualiza colaborador e persiste alteração")
    // Valida atualização de colaborador, garantindo que o novo cargo seja persistido e retorne um DTO
    void deveAtualizarColaboradorComSucesso() {
        // 1. Cria o DTO original e salva via Controller
        EmployeeRequestDTO originalDTO = new EmployeeRequestDTO(
            null, 
            "Maria Oliveira", 
            "12345678903", 
            "MG987654", 
            "Intern", 
            LocalDate.of(2022, 3, 15), 
            new BigDecimal("2000"),
            0,
            20,
            false, null, null, null, null, null, null
        );
        ResponseEntity<?> createResponse = employeeController.createEmployee(originalDTO, null);
        EmployeeResponseDTO savedDTO = (EmployeeResponseDTO) createResponse.getBody();
        Long employeeId = savedDTO.getId();

        // 2. Cria o DTO de Atualização com o novo valor
        EmployeeRequestDTO updateDTO = new EmployeeRequestDTO(
            employeeId,
            "Maria Oliveira", 
            "12345678903",    
            "MG987654", 
            "Junior Developer", // Valor a ser atualizado
            LocalDate.of(2022, 3, 15), 
            new BigDecimal("2000"),
            0,
            20,
            false, null, null, null, null, null, null
        );

        // 3. Chama o método de atualização
        ResponseEntity<?> response = employeeController.updateEmployee(employeeId, updateDTO);

        // Verifica o status de sucesso
        assertEquals(200, response.getStatusCode().value());
        
        // 4. Verifica se o corpo é o DTO e se o valor foi atualizado corretamente
        EmployeeResponseDTO updatedDTO = (EmployeeResponseDTO) response.getBody();
        assertEquals("Junior Developer", updatedDTO.getPosition());
    }

    @Test
    @DisplayName("Deleta colaborador com sucesso")
    // Valida exclusão de colaborador, verificando o retorno 200 e a mensagem de sucesso
    void deveDeletarColaboradorComSucesso() {
        // 1. Cria o DTO e salva via Controller para obter o ID
        EmployeeRequestDTO requestDTO = new EmployeeRequestDTO(
            null, 
            "João Santos", 
            "12345678904", 
            "MG112233", 
            "Analyst", 
            LocalDate.of(2019, 8, 20), 
            new BigDecimal("2800"),
            0,
            40,
            false, null, null, null, null, null, null
        );
        ResponseEntity<?> createResponse = employeeController.createEmployee(requestDTO, null);
        EmployeeResponseDTO savedDTO = (EmployeeResponseDTO) createResponse.getBody();
        Long employeeId = savedDTO.getId();

        // 2. Chama o método de exclusão
        ResponseEntity<?> response = employeeController.deleteEmployee(employeeId);

        // Verifica o status de sucesso e a mensagem
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Deletado com sucesso", response.getBody());
    }
}