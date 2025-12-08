package com.payroll.service;

/*
 * Interface de contrato para estratégias de cálculo de descontos.
 * Define a estrutura comum que todas as regras de dedução (INSS, IRRF, Vale Transporte, etc.)
 * devem implementar para serem processadas pelo motor de cálculo da folha.
 */

import java.math.BigDecimal;

import com.payroll.service.SheetCalculator.DescontoContext;

public interface IDesconto {

    // Executar a lógica de cálculo do desconto baseada no contexto financeiro fornecido
    BigDecimal calcular(DescontoContext ctx);

    // Definir a ordem de aplicação do desconto (valores menores são processados primeiro)
    default int prioridade() {
        return 0;
    }

    // Retornar o nome legível da regra de desconto para fins de log ou exibição
    default String nome() {
        return getClass().getSimpleName();
    }
}