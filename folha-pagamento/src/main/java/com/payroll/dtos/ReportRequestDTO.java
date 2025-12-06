package com.payroll.dtos;

public class ReportRequestDTO {
    private Long employeeId;
    private String referenceMonth;
    private String type;

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getReferenceMonth() { return referenceMonth; }
    public void setReferenceMonth(String referenceMonth) { this.referenceMonth = referenceMonth; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
