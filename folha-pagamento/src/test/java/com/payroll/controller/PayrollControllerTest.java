package com.payroll.controller;

import com.payroll.entity.Employee;
import com.payroll.entity.PayrollCalculation;
import com.payroll.entity.User;
import com.payroll.service.EmployeeService;
import com.payroll.service.PayrollService;
import com.payroll.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PayrollControllerTest {

    private PayrollController controller;
    private PayrollService payrollService;
    private EmployeeService employeeService;
    private UserService userService;

    @BeforeEach
    void setup() {
        // Instanciar os serviços 
        payrollService = new PayrollService() {
            private final Map<Long, PayrollCalculation> payrollMap = new HashMap<>();
            private long idCounter = 1;

            @Override
            public List<PayrollCalculation> getAllPayrolls() {
                return new ArrayList<>(payrollMap.values());
            }

            @Override
            public PayrollCalculation calculatePayroll(Long employeeId, String referenceMonth, Long userId) {
                PayrollCalculation pc = new PayrollCalculation();
                pc.setId(idCounter++);
                pc.setEmployeeId(employeeId);
                pc.setReferenceMonth(referenceMonth);
                pc.setCreatedByUserId(userId);
                pc.setNetSalary(new BigDecimal("2000.00"));
                payrollMap.put(pc.getId(), pc);
                return pc;
            }

            @Override
            public List<PayrollCalculation> getEmployeePayrolls(Long employeeId) {
                List<PayrollCalculation> list = new ArrayList<>();
                for (PayrollCalculation pc : payrollMap.values()) {
                    if (pc.getEmployeeId().equals(employeeId)) {
                        list.add(pc);
                    }
                }
                return list;
            }
        };

        employeeService = new EmployeeService() {
            private final Map<Long, Employee> employeeMap = new HashMap<>();

            @Override
            public Optional<Employee> getEmployeeById(Long id) {
                return Optional.ofNullable(employeeMap.get(id));
            }

            @Override
            public List<Employee> getAllEmployees() {
                return new ArrayList<>(employeeMap.values());
            }

            public void addEmployee(Employee emp) {
                employeeMap.put(emp.getId(), emp);
            }
        };

        userService = new UserService() {
            private final Map<String, User> userMap = new HashMap<>();

            @Override
            public Optional<User> findByUsername(String username) {
                return Optional.ofNullable(userMap.get(username));
            }

            public void addUser(User user) {
                userMap.put(user.getUsername(), user);
            }
        };

        controller = new PayrollController();
        // Injetar serviços manualmente
        controller.payrollService = payrollService;
        controller.employeeService = employeeService;
        controller.userService = userService;
    }

    @Test
    void testPayrollListEmpty() {
        ResponseEntity<List<PayrollCalculation>> response = controller.payrollList();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testCalculatePayrollSuccess() {
        // Preparar dados
        User user = new User();
        user.setId(10L);
        user.setUsername("usuario1");
        userService.addUser(user);

        Employee employee = new Employee();
        employee.setId(1L);
        employeeService.addEmployee(employee);

        // Simular usuário logado
        UserDetails userDetails = () -> "usuario1"; // getUsername()

        Map<String, String> request = new HashMap<>();
        request.put("employeeId", "1");
        request.put("referenceMonth", "2025-10");

        ResponseEntity<?> response = controller.calculatePayroll(request, userDetails);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof PayrollCalculation);
        PayrollCalculation calculation = (PayrollCalculation) response.getBody();
        assertEquals(1L, calculation.getEmployeeId());
        assertEquals("2025-10", calculation.getReferenceMonth());
        assertEquals(user.getId(), calculation.getCreatedByUserId());
        assertNotNull(calculation.getNetSalary());
    }

    @Test
    void testCalculatePayrollError() {
        // Request com employeeId inválido (não numérico)
        UserDetails userDetails = () -> "usuario1";

        Map<String, String> request = new HashMap<>();
        request.put("employeeId", "abc"); // inválido
        request.put("referenceMonth", "2025-10");

        ResponseEntity<?> response = controller.calculatePayroll(request, userDetails);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Erro ao calcular folha"));
    }

    @Test
    void testViewPayrollFound() {
        // Preparar folha cadastrada
        PayrollCalculation calc = payrollService.calculatePayroll(1L, "2025-10", 10L);

        ResponseEntity<?> response = controller.viewPayroll(calc.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(calc, response.getBody());
    }

    @Test
    void testViewPayrollNotFound() {
        ResponseEntity<?> response = controller.viewPayroll(999L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Folha de pagamento não encontrada", response.getBody());
    }

    @Test
    void testViewEmployeePayrollsFound() {
        // Criar employee
        Employee emp = new Employee();
        emp.setId(1L);
        emp.setName("João");
        employeeService.addEmployee(emp);

        // Criar folha de pagamento para ele
        PayrollCalculation pc = payrollService.calculatePayroll(1L, "2025-10", 10L);

        ResponseEntity<?> response = controller.viewEmployeePayrolls(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(emp, body.get("employee"));
        List<?> calculations = (List<?>) body.get("calculations");
        assertTrue(calculations.contains(pc));
    }

    @Test
    void testViewEmployeePayrollsNotFound() {
        ResponseEntity<?> response = controller.viewEmployeePayrolls(999L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Funcionário não encontrado", response.getBody());
    }
}
