package com.payroll.dtos;

/*
 * Objeto de Transferência de Dados (DTO) para solicitação de relatórios.
 * Encapsula os filtros selecionados pelo usuário na interface de geração,
 * definindo o escopo (funcionário, período) e o modelo do documento desejado.
 */

public class ReportRequestDTO {
    
    // Identificador do funcionário alvo (se o relatório for individual)
    private Long employeeId;
    
    // Mês de competência dos dados (Formato esperado: YYYY-MM)
    private String referenceMonth;
    
    // Define o modelo de relatório (ex: PAYROLL, VACATION, INCOME_TAX)
    private String type;

    // --- Getters e Setters ---

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getReferenceMonth() { return referenceMonth; }
    public void setReferenceMonth(String referenceMonth) { this.referenceMonth = referenceMonth; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}