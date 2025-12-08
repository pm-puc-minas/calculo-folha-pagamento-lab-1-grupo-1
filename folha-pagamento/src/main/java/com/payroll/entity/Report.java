package com.payroll.entity;

/*
 * Entidade de persistência para Relatórios gerados.
 * Armazena o histórico de arquivos (PDFs), metadados de geração e status,
 * permitindo auditoria e download posterior dos documentos processados.
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Contexto do Relatório ---
    
    @Column(name = "employee_id")
    private Long employeeId; // Mantém o ID histórico mesmo se o funcionário for removido

    @Column(name = "employee_name")
    private String employeeName; // Snapshot do nome no momento da geração

    @Column(name = "reference_month")
    private String referenceMonth;

    @Column(name = "report_type")
    private String reportType; // Ex: FOLHA_PAGAMENTO, FERIAS, RENDIMENTOS

    // --- Auditoria e Controle de Estado ---
    
    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id")
    private User generatedBy; // Rastreabilidade do usuário solicitante

    @Column(name = "status")
    private String status; // Estados possíveis: COMPLETED, PENDING, ERROR

    // --- Conteúdo do Arquivo ---
    
    @Lob // Large Object: Mapeia para BLOB no banco para armazenar o binário do PDF
    @Column(name = "file_content", length = 10000000)
    @com.fasterxml.jackson.annotation.JsonIgnore // Evita trafegar o arquivo pesado em listagens JSON (performance)
    private byte[] fileContent;

    public Report() {
        this.generatedAt = LocalDateTime.now();
    }

    // --- Getters e Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getReferenceMonth() { return referenceMonth; }
    public void setReferenceMonth(String referenceMonth) { this.referenceMonth = referenceMonth; }

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public User getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(User generatedBy) { this.generatedBy = generatedBy; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public byte[] getFileContent() { return fileContent; }
    public void setFileContent(byte[] fileContent) { this.fileContent = fileContent; }
}