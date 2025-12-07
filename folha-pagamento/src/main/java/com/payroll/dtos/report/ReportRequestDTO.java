package com.payroll.dtos.report;

import com.fasterxml.jackson.annotation.JsonAlias;

public class ReportRequestDTO {
    /**
     * Tipo do relatório. Aceita também "reportType" como alias no JSON.
     */
    @JsonAlias({"reportType"})
    private String type;

    private Long employeeId;

    /**
     * Mês de referência no formato yyyy-MM.
     * Aceita "month" como alias no JSON.
     */
    @JsonAlias({"month"})
    private String referenceMonth;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getReferenceMonth() { return referenceMonth; }
    public void setReferenceMonth(String referenceMonth) { this.referenceMonth = referenceMonth; }
}
