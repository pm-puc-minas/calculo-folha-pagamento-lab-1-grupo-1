package com.payroll.dtos.dashboard;

import java.util.List;

public class DashboardDTO {
    private String currentUser;
    private Long totalEmployees;
    private String lastPayrollDate; // Format: dd/MM/yyyy HH:mm
    private java.math.BigDecimal totalSalaries;
    private Long pendingCalculations; // Removed from UI but kept in DTO if needed, or can be ignored
    private List<SalaryDistributionDTO> salaryDistribution;
    private List<RecentEmployeeDTO> recentEmployees;
    private Long totalPayrolls;

    public DashboardDTO() {}

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
