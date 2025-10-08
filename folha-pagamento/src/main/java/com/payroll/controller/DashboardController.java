package com.payroll.controller;

import com.payroll.service.EmployeeService;
import com.payroll.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PayrollService payrollService;

    /**
     * Endpoint principal do dashboard.
     * Retorna estatísticas resumidas para o frontend.
     */
    @GetMapping
    public ResponseEntity<?> getDashboardData(@AuthenticationPrincipal UserDetails currentUser) {
        try {
            // Coleta de dados principais
            int totalEmployees = employeeService.getAllEmployees().size();
            int totalPayrolls = payrollService.getAllPayrolls().size();

            // Mapa de resposta (JSON)
            Map<String, Object> dashboardData = new HashMap<>();
            dashboardData.put("totalEmployees", totalEmployees);
            dashboardData.put("totalPayrolls", totalPayrolls);
            dashboardData.put("recentEmployees", employeeService.getAllEmployees());
            dashboardData.put("recentPayrolls", payrollService.getAllPayrolls());
            dashboardData.put("currentUser", currentUser != null ? currentUser.getUsername() : null);

            return ResponseEntity.ok(dashboardData);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Erro ao carregar dados do dashboard: " + e.getMessage()));
        }
    }
}

