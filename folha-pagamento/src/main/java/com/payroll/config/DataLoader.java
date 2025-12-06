package com.payroll.config;
/*
 * Componente de carga inicial de dados (seed).
 * Cria usuários padrão (admin e user) na inicialização
 * para facilitar testes e uso inicial do sistema.
 */

import com.payroll.entity.Employee;
import com.payroll.entity.User;
import com.payroll.service.EmployeeService;
import com.payroll.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public void run(String... args) throws Exception {
        // Criar usuário administrador padrão
        if (!userService.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@payroll.com");
            admin.setPassword("admin123");
            admin.setRole(User.Role.ADMIN);
            userService.createUser(admin, null);
            System.out.println("✅ Usuário administrador criado: admin/admin123");
        }

        // Criar usuário comum padrão
        if (!userService.existsByUsername("user")) {
            User user = new User();
            user.setUsername("user");
            user.setEmail("user@payroll.com");
            user.setPassword("user123");
            user.setRole(User.Role.USER);
            userService.createUser(user, null);
            System.out.println("✅ Usuário comum criado: user/user123");
        }

        // Criar funcionário de exemplo se não existir nenhum
        if (employeeService.getAllEmployees().isEmpty()) {
            Employee emp = new Employee();
            emp.setFullName("João Silva");
            emp.setCpf("123.456.789-00");
            emp.setRg("12.345.678-9");
            emp.setPosition("Desenvolvedor Senior");
            emp.setAdmissionDate(LocalDate.of(2023, 1, 15));
            emp.setSalary(new BigDecimal("8500.00"));
            emp.setDependents(2);
            emp.setWeeklyHours(40);
            emp.setTransportVoucher(true);
            emp.setTransportVoucherValue(new BigDecimal("250.00"));
            emp.setMealVoucher(true);
            emp.setMealVoucherValue(new BigDecimal("35.00"));
            emp.setHealthPlan(true);
            emp.setHealthPlanValue(new BigDecimal("150.00"));
            
            employeeService.createEmployee(emp, 1L); // Assuming admin (ID 1) created it
            System.out.println("✅ Funcionário de exemplo criado: João Silva");
        }
    }
}