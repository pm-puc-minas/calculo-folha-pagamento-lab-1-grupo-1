package com.payroll.controller;

import com.payroll.dtos.report.ReportRequestDTO;
import com.payroll.dtos.report.ReportResponseDTO;
import com.payroll.service.ReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    @Autowired
    private ReportsService reportsService;

    @GetMapping("/history")
    public ResponseEntity<List<ReportResponseDTO>> getHistory(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String referenceMonth,
            @RequestParam(required = false) String type) {
        return ResponseEntity.ok(reportsService.getHistory(employeeId, referenceMonth, type));
    }

    @PostMapping({"", "/", "/create"})
    public ResponseEntity<ReportResponseDTO> createReport(
            @RequestBody ReportRequestDTO request,
            Authentication authentication) {
        // Pode chegar sem autenticaçãõ, pois /api/reports é liberado no SecurityConfig.
        String username = authentication != null ? authentication.getName() : null;
        if (request.getEmployeeId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "employeeId é obrigatório");
        }
        String referenceMonth = request.getReferenceMonth();
        String type = request.getType();
        
        // If type is EMPLOYEE, referenceMonth might be null or current month
        if (referenceMonth == null || referenceMonth.isBlank()) {
            referenceMonth = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        if (type == null || type.isBlank()) {
            type = "PAYROLL";
        }

        return ResponseEntity.ok(
                reportsService.createReportDto(request.getEmployeeId(), referenceMonth, type, username));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadReport(@PathVariable Long id) {
        try {
            // Check type of report first? Or service handles it?
            // Since service methods are split, I need to know the type.
            // But I only have ID.
            // I should add a generic download method in Service that delegates.
            // For now, let's look up the report to check type.
            // Actually, let's add a generic method in service.
            // Wait, I can just query the report here.
            
            // Let's update Service to have a generic download method or expose getReport.
            // For simplicity, I'll rely on the service having a way.
            // I'll update ReportsService to have a `downloadReport(Long id)` method that checks type.
            
            byte[] content = reportsService.generateReportContent(id); // Need to add this to service
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(content);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Erro ao gerar relatorio: " + e.getMessage()).getBytes());
        }
    }

    @DeleteMapping({"/{id}", "/{id}/delete"})
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportsService.deleteReport(id);
        return ResponseEntity.ok().build();
    }
}
