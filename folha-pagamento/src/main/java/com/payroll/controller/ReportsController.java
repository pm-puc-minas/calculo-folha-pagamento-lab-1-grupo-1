package com.payroll.controller;

import com.payroll.entity.Report;
import com.payroll.service.ReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    @Autowired
    private ReportsService reportsService;

    @GetMapping("/history")
    public ResponseEntity<List<Report>> getHistory(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String referenceMonth,
            @RequestParam(required = false) String type) {
        return ResponseEntity.ok(reportsService.getHistory(employeeId, referenceMonth, type));
    }

    @PostMapping("/create")
    public ResponseEntity<Report> createReport(
            @RequestBody com.payroll.dtos.ReportRequestDTO request,
            Authentication authentication) {
        String username = authentication.getName();
        String referenceMonth = request.getReferenceMonth();
        
        // If type is EMPLOYEE, referenceMonth might be null or current month
        if (referenceMonth == null) {
            referenceMonth = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        return ResponseEntity.ok(reportsService.createReport(request.getEmployeeId(), referenceMonth, request.getType(), username));
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

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportsService.deleteReport(id);
        return ResponseEntity.ok().build();
    }
}
