package com.payroll.dtos.report;

import java.time.LocalDateTime;

public class ReportResponseDTO {
    private Long id;
    private String reportType;
    private String employeeName;
    private String referenceMonth;
    private LocalDateTime generatedAt;
    private String status;
    private GeneratedByDTO generatedBy;

    public static class GeneratedByDTO {
        private Long id;
        private String name;
        private String role = "User"; // Default

        public GeneratedByDTO(Long id, String name) {
            this.id = id;
            this.name = name;
        }
        // getters setters
        public Long getId() { return id; }
        public String getName() { return name; }
        public String getRole() { return role; }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getReferenceMonth() { return referenceMonth; }
    public void setReferenceMonth(String referenceMonth) { this.referenceMonth = referenceMonth; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public GeneratedByDTO getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(GeneratedByDTO generatedBy) { this.generatedBy = generatedBy; }
}
