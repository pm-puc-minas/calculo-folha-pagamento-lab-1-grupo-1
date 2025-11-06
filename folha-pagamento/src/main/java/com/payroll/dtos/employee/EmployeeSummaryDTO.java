package com.payroll.dtos.employee;

// DTO (Data Transfer Object) para representar um resumo do funcionário.
// Usado em listas ou locais onde apenas informações básicas são necessárias.
public class EmployeeSummaryDTO {
    
    // Dados essenciais de identificação e cargo
    private Long id;
    private String fullName;
    private String position;

    // Construtor padrão
    public EmployeeSummaryDTO() {}

    // Construtor com campos
    public EmployeeSummaryDTO(Long id, String fullName, String position) {
        this.id = id;
        this.fullName = fullName;
        this.position = position;
    }
    
    // Bloco de Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
}