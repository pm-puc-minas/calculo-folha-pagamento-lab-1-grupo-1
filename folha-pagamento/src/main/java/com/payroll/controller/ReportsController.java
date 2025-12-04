package com.payroll.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * Endpoints de relatórios e histórico (stub simples para integrar com o front).
 */
@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:5173") // front local
public class ReportsController {

    @GetMapping
    public ResponseEntity<?> listReports(@AuthenticationPrincipal UserDetails currentUser) {
        // Stub de exemplo
        List<Map<String, Object>> reports = List.of(
                Map.of("id", 1, "type", "payroll", "name", "Relatorio Folha Mensal", "generatedAt", OffsetDateTime.now()),
                Map.of("id", 2, "type", "employee", "name", "Relatorio Funcionarios", "generatedAt", OffsetDateTime.now().minusDays(2))
        );
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/history")
    public ResponseEntity<?> history(@AuthenticationPrincipal UserDetails currentUser) {
        // Stub de histórico
        List<Map<String, Object>> history = List.of(
                Map.of("id", "h1", "reportType", "payroll", "referenceMonth", "2024-10", "status", "completed", "generatedAt", OffsetDateTime.now().minusHours(3)),
                Map.of("id", "h2", "reportType", "employee", "referenceMonth", "2024-09", "status", "completed", "generatedAt", OffsetDateTime.now().minusDays(1))
        );
        return ResponseEntity.ok(history);
    }
}
