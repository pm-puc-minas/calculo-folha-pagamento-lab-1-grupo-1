package com.payroll.dtos.employee;

/*
 * Objeto de Transferência de Dados (DTO) resumido para Funcionários.
 * Contém apenas as informações essenciais (ID, nome e cargo) para popular
 * listas de seleção (dropdowns), autocompletar e visualizações compactas,
 * otimizando o tráfego de dados em operações de leitura.
 */

public class EmployeeSummaryDTO {
    
    private Long id;
    private String fullName;
    private String position;

    public EmployeeSummaryDTO() {}

    // Construtor utilitário para projeções de banco de dados (JPQL)
    public EmployeeSummaryDTO(Long id, String fullName, String position) {
        this.id = id;
        this.fullName = fullName;
        this.position = position;
    }
    
    // --- Getters e Setters ---
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
}