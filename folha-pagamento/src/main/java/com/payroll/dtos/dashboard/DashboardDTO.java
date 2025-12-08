package com.payroll.dtos.dashboard;

/*
 * Objeto de Transferência de Dados (DTO) para o Dashboard Principal.
 * Agrega indicadores chave de desempenho (KPIs), métricas financeiras
 * e resumos estatísticos para fornecer uma visão geral do sistema na tela inicial.
 */

import java.util.List;

public class DashboardDTO {
    
    // Identificação do contexto do usuário
    private String currentUser;
    
    // Métricas gerais de contagem e valores monetários
    private Long totalEmployees;
    private java.math.BigDecimal totalSalaries;
    private Long totalPayrolls;
    
    // Informações de status e controle temporal
    private String lastPayrollDate; // Formato esperado pelo front: dd/MM/yyyy HH:mm
    private Long pendingCalculations; // Mantido para compatibilidade, indica pendências de processamento
    
    // Estruturas de dados complexas para gráficos e listas
    private List<SalaryDistributionDTO> salaryDistribution;
    private List<RecentEmployeeDTO> recentEmployees;

    public DashboardDTO() {}

    // --- Getters e Setters ---

    public java.math.BigDecimal getTotalSalaries() { return totalSalaries; }
    public void setTotalSalaries(java.math.BigDecimal totalSalaries) { this.totalSalaries = totalSalaries; }

    public String getCurrentUser() { return currentUser; }
    public void setCurrentUser(String currentUser) { this.currentUser = currentUser; }

    public Long getTotalEmployees() { return totalEmployees; }
    public void setTotalEmployees(Long totalEmployees) { this.totalEmployees = totalEmployees; }

    public String getLastPayrollDate() { return lastPayrollDate; }
    public void setLastPayrollDate(String lastPayrollDate) { this.lastPayrollDate = lastPayrollDate; }

    public Long getPendingCalculations() { return pendingCalculations; }
    public void setPendingCalculations(Long pendingCalculations) { this.pendingCalculations = pendingCalculations; }

    public List<SalaryDistributionDTO> getSalaryDistribution() { return salaryDistribution; }
    public void setSalaryDistribution(List<SalaryDistributionDTO> salaryDistribution) { this.salaryDistribution = salaryDistribution; }

    public List<RecentEmployeeDTO> getRecentEmployees() { return recentEmployees; }
    public void setRecentEmployees(List<RecentEmployeeDTO> recentEmployees) { this.recentEmployees = recentEmployees; }

    public Long getTotalPayrolls() { return totalPayrolls; }
    public void setTotalPayrolls(Long totalPayrolls) { this.totalPayrolls = totalPayrolls; }
}