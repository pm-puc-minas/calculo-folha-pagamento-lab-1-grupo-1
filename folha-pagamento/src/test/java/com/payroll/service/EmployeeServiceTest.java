package com.payroll.service;

import com.payroll.entity.Employee;
import com.payroll.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employee = new Employee();
        employee.setFullName("Bernardo");
        employee.setCpf("12345678900");
        employee.setRg("MG123456");
        employee.setPosition("Developer");
        employee.setAdmissionDate(LocalDate.of(2020, 1, 1));
        employee.setSalary(new BigDecimal("5000"));
        employee.setWeeklyHours(40);
        employee.setTransportVoucher(true);
        employee.setMealVoucher(true);
        employee.setMealVoucherValue(new BigDecimal("500"));
        employee.setDangerousWork(false);
        employee.setDangerousPercentage(BigDecimal.ZERO);
        employee.setUnhealthyWork(false);
        employee.setUnhealthyLevel(null);
    }

    // Testa a criação de um funcionário
    @Test
    void testCreateEmployee() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        Employee created = employeeService.createEmployee(employee, 1L);

        assertNotNull(created);
        assertEquals("Bernardo", created.getFullName());
        assertEquals(1L, created.getCreatedBy());
        verify(employeeRepository, times(1)).save(employee);
    }

    // Testa a listagem de todos os funcionários
    @Test
    void testGetAllEmployees() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee));

        List<Employee> employees = employeeService.getAllEmployees();

        assertEquals(1, employees.size());
        verify(employeeRepository, times(1)).findAll();
    }

    // Testa a busca por ID
    @Test
    void testGetEmployeeById() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        Optional<Employee> result = employeeService.getEmployeeById(1L);

        assertTrue(result.isPresent());
        assertEquals("Bernardo", result.get().getFullName());
    }

    // Testa a busca por CPF
    @Test
    void testGetEmployeeByCpf() {
        when(employeeRepository.findByCpf("12345678900")).thenReturn(Optional.of(employee));

        Optional<Employee> result = employeeService.getEmployeeByCpf("12345678900");

        assertTrue(result.isPresent());
        assertEquals("Bernardo", result.get().getFullName());
    }

    // Testa se existe um funcionário por CPF
    @Test
    void testExistsByCpf() {
        when(employeeRepository.existsByCpf("12345678900")).thenReturn(true);

        boolean exists = employeeService.existsByCpf("12345678900");

        assertTrue(exists);
    }

    // Testa a atualização de um funcionário
    @Test
    void testUpdateEmployee() {
        Employee updatedDetails = new Employee();
        updatedDetails.setFullName("Gustavo");
        updatedDetails.setCpf("09876543211");
        updatedDetails.setRg("MG654321");
        updatedDetails.setPosition("Tester");
        updatedDetails.setAdmissionDate(LocalDate.of(2021, 2, 1));
        updatedDetails.setSalary(new BigDecimal("6000"));
        updatedDetails.setWeeklyHours(35);
        updatedDetails.setTransportVoucher(false);
        updatedDetails.setMealVoucher(false);
        updatedDetails.setMealVoucherValue(BigDecimal.ZERO);
        updatedDetails.setDangerousWork(true);
        updatedDetails.setDangerousPercentage(new BigDecimal("10"));
        updatedDetails.setUnhealthyWork(true);
        updatedDetails.setUnhealthyLevel("ALTO");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedDetails);

        Employee result = employeeService.updateEmployee(1L, updatedDetails);

        assertEquals("Gustavo", result.getFullName());
        assertEquals("09876543211", result.getCpf());
        assertTrue(result.getDangerousWork());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    // Testa a exclusão de um funcionário
    @Test
    void testDeleteEmployee() {
        doNothing().when(employeeRepository).deleteById(1L);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository, times(1)).deleteById(1L);
    }
}
