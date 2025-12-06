package com.payroll.controller;

import com.payroll.dtos.dashboard.DashboardDTO;
import com.payroll.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController implements IDashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Endpoint principal do dashboard.
     * Retorna estatísticas resumidas para o frontend.
     */
    @GetMapping
    @Override
    public ResponseEntity<?> getDashboardData(@AuthenticationPrincipal UserDetails currentUser) {
        try {
            String username = currentUser != null ? currentUser.getUsername() : "Visitante";
            DashboardDTO dashboardData = dashboardService.getDashboardData(username);
            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Erro ao carregar dados do dashboard: " + e.getMessage());
        }
    }
}

