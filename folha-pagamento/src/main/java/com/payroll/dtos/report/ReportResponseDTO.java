package com.payroll.dtos.report;

/*
 * Objeto de Transferência de Dados (DTO) para resposta de relatórios.
 * Representa o registro histórico de um relatório gerado, contendo metadados
 * sobre quem solicitou, quando foi processado e o status da geração (sucesso/erro).
 */

import java.time.LocalDateTime;

public class ReportResponseDTO {

    private Long id;
    private String reportType;     // Tipo do relatório (ex: FOLHA, FERIAS)
    private String employeeName;   // Nome do funcionário alvo do relatório
    private String referenceMonth; // Mês de referência dos dados
    private LocalDateTime generatedAt;
    private String status;         // Status do processamento (ex: COMPLETED)
    
    // Objeto aninhado com dados do responsável pela geração
    private GeneratedByDTO generatedBy;

    /*
     * DTO interno estático para encapsular dados básicos do usuário gerador.
     * Evita expor a entidade User completa na resposta do relatório.
     */
    public static class GeneratedByDTO {
        private Long id;
        private String name;
        private String role = "User"; // Valor padrão caso não seja informado

        public GeneratedByDTO(Long id, String name) {
            this.id = id;
            this.name = name;
        }
        
        // --- Getters e Setters Internos ---
        public Long getId() { return id; }
        public String getName() { return name; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    // --- Getters e Setters Principais ---

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