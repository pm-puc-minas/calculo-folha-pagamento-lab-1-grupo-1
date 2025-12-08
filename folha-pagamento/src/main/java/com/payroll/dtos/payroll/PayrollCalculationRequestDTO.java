package com.payroll.dtos.payroll;

/*
 * Objeto de Transferência de Dados (DTO) para solicitação de cálculo de folha.
 * Encapsula os parâmetros necessários para acionar o motor de cálculo,
 * identificando o funcionário alvo e o período de competência (referência).
 */

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public class PayrollCalculationRequestDTO {

    // Identificador único do funcionário a ser processado
    @NotNull(message = "O ID do funcionário é obrigatório.")
    @Min(value = 1, message = "O ID do funcionário deve ser válido.")
    private Long employeeId;

    // Período de competência para o cálculo (Formato esperado: YYYY-MM)
    @NotBlank(message = "O mês de referência é obrigatório.")
    private String referenceMonth;
    
    public PayrollCalculationRequestDTO() {}

    // Construtor utilitário para testes e chamadas internas
    public PayrollCalculationRequestDTO(Long employeeId, String referenceMonth) {
        this.employeeId = employeeId;
        this.referenceMonth = referenceMonth;
    }

    // --- Getters e Setters ---

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