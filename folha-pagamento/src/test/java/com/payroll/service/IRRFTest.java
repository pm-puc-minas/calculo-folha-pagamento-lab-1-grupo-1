package com.payroll.service;

import com.payroll.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions; // Import corrigido para Assertions.assertEquals

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IRPFTest {

    // Instâncias das Estratégias
    private final INSS inssStrategy = new INSS();
    private final IRPF irpfStrategy = new IRPF();

    /**
     * Auxiliar que simula o fluxo do PayrollService: 
     * 1. Cria o Contexto. 2. Executa INSS. 3. Executa IRRF.
     */
    private BigDecimal calcularIRPF_noFluxo(BigDecimal salarioBruto, int dependentes, BigDecimal pensao) {
        // 1. Cria o Contexto com os dados iniciais
        // O construtor correto é: (salarioBruto, dependentes, pensaoAlimenticia)
        SheetCalculator.DescontoContext ctx = new SheetCalculator.DescontoContext(
                salarioBruto, dependentes, pensao
        );

        // 2. Executa o INSS (Prioridade 1) - Ele *atualizará* a base de cálculo no Contexto (ctx)
        inssStrategy.calcular(ctx);

        // 3. Executa o IRPF (Prioridade 2) - Ele usará a base de cálculo já reduzida pelo INSS
        return irpfStrategy.calcular(ctx).setScale(2, RoundingMode.HALF_UP);
    }

    @Test
    @DisplayName("calcularIRPF_deveAplicarFormulaCorreta_comSalarioDe3000")
    // Testa o cálculo do IRPF usando o fluxo Strategy (INSS -> IRPF) e a fórmula Base*Aliquota-ParcelaADeduzir.
    void calcularIRPF_deveAplicarFormulaCorreta_comSalarioDe3000() {
        BigDecimal salarioBruto = new BigDecimal("3000.00");
        
        // --- Cálculo de referência para Salário R$ 3.000,00:
        // INSS (R$ 258,82). Base IRRF = R$ 2741,18

        // Dependentes = 0 (Valor esperado = R$ 68,26)
        BigDecimal irpf0 = calcularIRPF_noFluxo(salarioBruto, 0, BigDecimal.ZERO);
        assertEquals(new BigDecimal("68.26"), irpf0);

        // Dependentes = 2 (Valor esperado = R$ 40,84)
        BigDecimal irpf2 = calcularIRPF_noFluxo(salarioBruto, 2, BigDecimal.ZERO);
        assertEquals(new BigDecimal("40.84"), irpf2);

        // Dependentes = 3 (Isento)
        // Base após deduções de dependentes é R$ 2172,41, que é menor que a isenção.
        BigDecimal irpf3 = calcularIRPF_noFluxo(salarioBruto, 3, BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, irpf3); 
        
        // Exemplo com valor alto para testar a faixa máxima (27.5%)
        BigDecimal salarioAlto = new BigDecimal("8000.00");
        // INSS (Teto R$ 877,22). Base IRRF = R$ 7122,78. Valor esperado: R$ 863.63
        BigDecimal irpfAlto = calcularIRPF_noFluxo(salarioAlto, 0, BigDecimal.ZERO);
        assertEquals(new BigDecimal("863.63"), irpfAlto);
    }
}