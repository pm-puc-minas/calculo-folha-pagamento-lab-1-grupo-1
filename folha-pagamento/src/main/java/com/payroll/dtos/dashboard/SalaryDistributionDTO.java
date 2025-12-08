package com.payroll.dtos.dashboard;

/*
 * Objeto de Transferência de Dados (DTO) para distribuição salarial.
 * Estrutura dados estatísticos para alimentação de gráficos, relacionando 
 * faixas de valores (labels) com a quantidade de funcionários (dados).
 */

public class SalaryDistributionDTO {
    
    // Rótulo descritivo da faixa salarial (ex: "R$ 2.000 - R$ 3.000")
    private String range;
    
    // Contagem absoluta de funcionários enquadrados nesta faixa
    private Long count;

    public SalaryDistributionDTO() {}

    // Construtor utilitário para instanciação direta em consultas de agregação (JPQL/SQL)
    public SalaryDistributionDTO(String range, Long count) {
        this.range = range;
        this.count = count;
    }

    // --- Getters e Setters ---

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}