package com.payroll.controller;

import com.payroll.entity.Employee;
import com.payroll.repository.EmployeeRepository;
import com.payroll.repository.PayrollCalculationRepository;
import com.payroll.service.EmployeeService;
import com.payroll.service.PayrollService;
import com.payroll.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PayrollControllerTest {

    @Autowired
    private PayrollCalculationRepository payrollRepository; 
    @Autowired
    private EmployeeRepository employeeRepository; 

    private PayrollService payrollService;
    private EmployeeService employeeService;
    private UserService userService;
    private PayrollController controller;

    @BeforeEach
    void setup() throws Exception {
        payrollService = new PayrollService(); 
        employeeService = new EmployeeService(); 
        userService = new UserService(); 
        controller = new PayrollController(); 

        // Injetar repositories nos services via Reflection
        java.lang.reflect.Field payrollRepoField = PayrollService.class.getDeclaredField("payrollRepository");
        payrollRepoField.setAccessible(true);
        payrollRepoField.set(payrollService, payrollRepository);

        java.lang.reflect.Field employeeRepoField = EmployeeService.class.getDeclaredField("employeeRepository");
        employeeRepoField.setAccessible(true);
        employeeRepoField.set(employeeService, employeeRepository);

        // Injetar services no controller via Reflection
        java.lang.reflect.Field payrollServiceField = PayrollController.class.getDeclaredField("payrollService");
        payrollServiceField.setAccessible(true);
        payrollServiceField.set(controller, payrollService);

        java.lang.reflect.Field employeeServiceField = PayrollController.class.getDeclaredField("employeeService");
        employeeServiceField.setAccessible(true);
        employeeServiceField.set(controller, employeeService);

        java.lang.reflect.Field userServiceField = PayrollController.class.getDeclaredField("userService");
        userServiceField.setAccessible(true);
        userServiceField.set(controller, userService);
    }

    @Test
    void testPayrollListEmpty() {
        ResponseEntity<?> response = controller.payrollList();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((List<?>) response.getBody()).isEmpty());
    }

    @Test
    void testCalculatePayrollError() {
        Map<String, String> request = new HashMap<>();
        request.put("employeeId", "abc"); // inválido
        request.put("referenceMonth", "2025-10");

        ResponseEntity<?> response = controller.calculatePayroll(request, null); // usado null
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Erro ao calcular folha"));
    }

    @Test
    void testViewPayrollNotFound() {
        ResponseEntity<?> response = controller.viewPayroll(999L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Folha de pagamento não encontrada", response.getBody());
    }

    @Test
    void testViewEmployeePayrollsNotFound() {
        ResponseEntity<?> response = controller.viewEmployeePayrolls(999L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Funcionário não encontrado", response.getBody());
    }

    @Test
    void testCreateEmployeeAndCalculatePayroll() {
        // Criar Employee válido
        Employee emp = new Employee();
        emp.setCpf("12345678901"); 
        emp.setFullName("Bernardo Pereira"); 
        emp.setRg("MG1234567"); 
        emp.setPosition("Developer"); 
        emp.setSalary(BigDecimal.valueOf(3000.0)); 
        emp.setWeeklyHours(40); 
        emp.setAdmissionDate(LocalDate.now()); 

        Employee saved = employeeService.createEmployee(emp, null);

        // Validar que foi salvo
        assertNotNull(saved.getId());

        // Agora calcular folha
        Map<String, String> request = new HashMap<>();
        request.put("employeeId", saved.getId().toString());
        request.put("referenceMonth", "2025-10");

        ResponseEntity<?> payrollResponse = controller.calculatePayroll(request, null); // usado null
        assertEquals(HttpStatus.OK, payrollResponse.getStatusCode());
        assertNotNull(payrollResponse.getBody());
    }
}
