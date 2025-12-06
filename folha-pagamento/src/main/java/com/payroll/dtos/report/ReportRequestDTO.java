package com.payroll.dtos.report;

public class ReportRequestDTO {
    private String type;
    private Long employeeId;
    private String month;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
}
