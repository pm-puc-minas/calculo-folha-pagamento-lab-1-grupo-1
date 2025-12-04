package com.payroll.service;

import com.payroll.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IRPFTest {

    // Instâncias das Estratégias
    private final INSS inssStrategy = new INSS();
    private final IRPF irpfStrategy = new IRPF();

    /**
     * Auxiliar para calcular INSS e atualizar o Contexto antes de calcular o IRPF.
     * Simula o fluxo de execução do PayrollService.
     */
    private BigDecimal calcularIRPF_noFluxo(BigDecimal salarioBruto, int dependentes, BigDecimal pensao) {
        // 1. Cria o Contexto com os dados iniciais
        SheetCalculator.DescontoContext ctx = new SheetCalculator.DescontoContext(
                salarioBruto, dependentes, pensao
        );

        // 2. Executa o INSS (Prioridade 1) - Ele atualizará o ctx.salarioBaseCalculo
        inssStrategy.calcular(ctx);

        // 3. Executa o IRPF (Prioridade 2) - Ele usará o ctx.salarioBaseCalculo já reduzido
        return irpfStrategy.calcular(ctx).setScale(2, RoundingMode.HALF_UP);
    }

    @Test
    @DisplayName("calcularIRPF_deveAplicarFormulaCorreta_comSalarioDe3000")
    // Testa o cálculo do IRPF usando o fluxo completo (INSS -> IRPF) e a fórmula correta.
    void calcularIRPF_deveAplicarFormulaCorreta_comSalarioDe3000() {
        // Salário de R$ 3.000,00 (Exemplo de entrada)
        BigDecimal salarioBruto = new BigDecimal("3000.00");
        
        // --- INSS para R$ 3.000,00 é R$ 258,82
        // --- Base de cálculo (sem dependentes) = 3000 - 258.82 = R$ 2741,18

        // Dependentes = 0 (Valor esperado = R$ 68,26)
        // Base (2741.18) cai na faixa de 7.5% com parcela a deduzir.
        BigDecimal irpf0 = calcularIRPF_noFluxo(salarioBruto, 0, BigDecimal.ZERO);
        assertEquals(new BigDecimal("68.26"), irpf0);

        // Dependentes = 2 (Valor esperado = R$ 40,84)
        // Base (2741.18 - (2 * 189.59)) = R$ 2361,99
        BigDecimal irpf2 = calcularIRPF_noFluxo(salarioBruto, 2, BigDecimal.ZERO);
        assertEquals(new BigDecimal("40.84"), irpf2);

        // Dependentes = 3 (Isento)
        // Base (2741.18 - (3 * 189.59)) = R$ 2172,41 (abaixo da isenção de R$ 2259,20)
        BigDecimal irpf3 = calcularIRPF_noFluxo(salarioBruto, 3, BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, irpf3);
    }
    
    @Test
    @DisplayName("calcularIRPF_deveZerar_seBaseMenorIsencao")
    // Testa um salário baixo que deve resultar em IRPF zero.
    void calcularIRPF_deveZerar_seBaseMenorIsencao() {
        BigDecimal salarioBruto = new BigDecimal("2500.00"); // Base deve cair na isenção após INSS

        BigDecimal irpf = calcularIRPF_noFluxo(salarioBruto, 0, BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, irpf);
    }
}