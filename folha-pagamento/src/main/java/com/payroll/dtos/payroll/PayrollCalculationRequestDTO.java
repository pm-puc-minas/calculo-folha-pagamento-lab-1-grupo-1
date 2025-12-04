package com.payroll.dtos.payroll;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;


public class PayrollCalculationRequestDTO {

    @NotNull(message = "O ID do funcionário é obrigatório.")
    @Min(value = 1, message = "O ID do funcionário deve ser válido.")
    private Long employeeId;

    @NotBlank(message = "O mês de referência é obrigatório.")
    private String referenceMonth;
    
    public PayrollCalculationRequestDTO() {}

    public PayrollCalculationRequestDTO(Long employeeId, String referenceMonth) {
        this.employeeId = employeeId;
        this.referenceMonth = referenceMonth;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getReferenceMonth() {
        return referenceMonth;
    }

    public void setReferenceMonth(String referenceMonth) {
        this.referenceMonth = referenceMonth;
    }
}