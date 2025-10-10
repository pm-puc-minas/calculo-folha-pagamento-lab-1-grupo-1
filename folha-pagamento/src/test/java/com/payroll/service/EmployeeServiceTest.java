package com.payroll.service;

import com.payroll.entity.Employee;
import com.payroll.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceTest {

    private EmployeeService employeeService;
    private FakeEmployeeRepository fakeEmployeeRepository;

    // Fake repository simples que armazena Employees numa lista em memória
    static class FakeEmployeeRepository implements EmployeeRepository {

        private Map<Long, Employee> storage = new HashMap<>();
        private AtomicLong idGenerator = new AtomicLong(1);

        @Override
        public <S extends Employee> S save(S employee) {
            if (employee.getId() == null) {
                employee.setId(idGenerator.getAndIncrement());
            }
            storage.put(employee.getId(), employee);
            return employee;
        }

        @Override
        public List<Employee> findAll() {
            return new ArrayList<>(storage.values());
        }

        @Override
        public Optional<Employee> findById(Long id) {
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public Optional<Employee> findByCpf(String cpf) {
            return storage.values().stream()
                    .filter(e -> cpf.equals(e.getCpf()))
                    .findFirst();
        }

        @Override
        public boolean existsByCpf(String cpf) {
            return storage.values().stream()
                    .anyMatch(e -> cpf.equals(e.getCpf()));
        }

        @Override
        public void deleteById(Long id) {
            storage.remove(id);
        }

        // Métodos não implementados podem lançar UnsupportedOperationException
        // para garantir que não são usados por engano
    }

    @BeforeEach
    void setUp() {
        fakeEmployeeRepository = new FakeEmployeeRepository();
        employeeService = new EmployeeService();
        employeeService.employeeRepository = fakeEmployeeRepository; // injetar fake repo
    }

    // Cria um funcionário padrão para testes
    private Employee createSampleEmployee() {
        Employee emp = new Employee();
        emp.setFullName("Maria Silva");
        emp.setCpf("11122233344");
        emp.setRg("MG1234567");
        emp.setPosition("Analista");
        emp.setAdmissionDate(LocalDate.of(2020, 1, 15));
        emp.setSalary(new BigDecimal("3500.00"));
        emp.setWeeklyHours(40);
        emp.setTransportVoucher(true);
        emp.setMealVoucher(true);
        emp.setMealVoucherValue(new BigDecimal("25.00"));
        emp.setDangerousWork(false);
        emp.setDangerousPercentage(0);
        emp.setUnhealthyWork(false);
        emp.setUnhealthyLevel(0);
        return emp;
    }

    @Test
    void testCreateEmployee() {
        Employee emp = createSampleEmployee();
        Employee saved = employeeService.createEmployee(emp, 10L);

        assertNotNull(saved.getId());
        assertEquals("Maria Silva", saved.getFullName());
        assertEquals(10L, saved.getCreatedBy());
    }

    @Test
    void testGetAllEmployees() {
        Employee emp1 = createSampleEmployee();
        Employee emp2 = createSampleEmployee();
        emp2.setCpf("55566677788");
        emp2.setFullName("João Souza");

        employeeService.createEmployee(emp1, 1L);
        employeeService.createEmployee(emp2, 2L);

        List<Employee> all = employeeService.getAllEmployees();

        assertEquals(2, all.size());
        assertTrue(all.stream().anyMatch(e -> e.getFullName().equals("Maria Silva")));
        assertTrue(all.stream().anyMatch(e -> e.getFullName().equals("João Souza")));
    }

    @Test
    void testGetEmployeeById() {
        Employee emp = createSampleEmployee();
        Employee saved = employeeService.createEmployee(emp, 5L);

        Optional<Employee> found = employeeService.getEmployeeById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Maria Silva", found.get().getFullName());
    }

    @Test
    void testGetEmployeeByCpf() {
        Employee emp = createSampleEmployee();
        employeeService.createEmployee(emp, 3L);

        Optional<Employee> found = employeeService.getEmployeeByCpf("11122233344");
        assertTrue(found.isPresent());
        assertEquals("Maria Silva", found.get().getFullName());

        Optional<Employee> notFound = employeeService.getEmployeeByCpf("00000000000");
        assertTrue(notFound.isEmpty());
    }

    @Test
    void testExistsByCpf() {
        Employee emp = createSampleEmployee();
        employeeService.createEmployee(emp, 3L);

        assertTrue(employeeService.existsByCpf("11122233344"));
        assertFalse(employeeService.existsByCpf("99999999999"));
    }

    @Test
    void testUpdateEmployee() {
        Employee emp = createSampleEmployee();
        Employee saved = employeeService.createEmployee(emp, 2L);

        Employee updateDetails = new Employee();
        updateDetails.setFullName("Maria Souza");
        updateDetails.setCpf("11122233344");
        updateDetails.setRg("MG7654321");
        updateDetails.setPosition("Gerente");
        updateDetails.setAdmissionDate(LocalDate.of(2019, 5, 10));
        updateDetails.setSalary(new BigDecimal("4500.00"));
        updateDetails.setWeeklyHours(38);
        updateDetails.setTransportVoucher(false);
        updateDetails.setMealVoucher(true);
        updateDetails.setMealVoucherValue(new BigDecimal("30.00"));
        updateDetails.setDangerousWork(true);
        updateDetails.setDangerousPercentage(10);
        updateDetails.setUnhealthyWork(true);
        updateDetails.setUnhealthyLevel(2);

        Employee updated = employeeService.updateEmployee(saved.getId(), updateDetails);

        assertEquals("Maria Souza", updated.getFullName());
        assertEquals("MG7654321", updated.getRg());
        assertEquals("Gerente", updated.getPosition());
        assertEquals(LocalDate.of(2019, 5, 10), updated.getAdmissionDate());
        assertEquals(new BigDecimal("4500.00"), updated.getSalary());
        assertEquals(38, updated.getWeeklyHours());
        assertFalse(updated.isTransportVoucher());
        assertTrue(updated.isMealVoucher());
        assertEquals(new BigDecimal("30.00"), updated.getMealVoucherValue());
        assertTrue(updated.isDangerousWork());
        assertEquals(10, updated.getDangerousPercentage());
        assertTrue(updated.isUnhealthyWork());
        assertEquals(2, updated.getUnhealthyLevel());
    }

    @Test
    void testDeleteEmployee() {
        Employee emp = createSampleEmployee();
        Employee saved = employeeService.createEmployee(emp, 1L);

        employeeService.deleteEmployee(saved.getId());
        Optional<Employee> found = employeeService.getEmployeeById(saved.getId());
        assertTrue(found.isEmpty());
    }
}
