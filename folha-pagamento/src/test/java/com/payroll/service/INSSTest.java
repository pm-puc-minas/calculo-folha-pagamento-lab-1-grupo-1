package com.payroll.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

class INSSTest {

    // Instância da Estratégia de INSS
    private final INSS inssStrategy = new INSS();

    @Test
    @DisplayName("calcularINSS_deveAplicarAliquotaSimples_naPrimeiraFaixa")
    // Testa um salário na primeira faixa para garantir 7.5% de desconto
    void calcularINSS_deveAplicarAliquotaSimples_naPrimeiraFaixa() {
        BigDecimal salarioBruto = new BigDecimal("1000.00");
        
        // Construtor do Contexto ajustado: (salarioBruto, dependentes, pensaoAlimenticia)
        SheetCalculator.DescontoContext ctx = new SheetCalculator.DescontoContext(
                salarioBruto, 0, BigDecimal.ZERO
        );

        BigDecimal desconto = inssStrategy.calcular(ctx);
        // Desconto esperado: 1000.00 * 0.075 = 75.00
        assertEquals(new BigDecimal("75.00").setScale(2, RoundingMode.HALF_UP), desconto);
    }
    
    @Test
    @DisplayName("calcularINSS_deveSerProgressivo_noExemploDe3000Reais")
    // Calcula INSS progressivo nas faixas de 2024 (R$ 3.000,00)
    void calcularINSS_deveSerProgressivo_noExemploDe3000Reais() {
        BigDecimal salarioBruto = new BigDecimal("3000.00");
        
        // O valor correto para R$ 3.000,00 em 2024 é R$ 258,82
        BigDecimal valorEsperado = new BigDecimal("258.82"); 

        // Construtor do Contexto ajustado: (salarioBruto, dependentes, pensaoAlimenticia)
        SheetCalculator.DescontoContext ctx = new SheetCalculator.DescontoContext(
                salarioBruto, 0, BigDecimal.ZERO
        );

        BigDecimal desconto = inssStrategy.calcular(ctx);
        // O teste deve garantir que o valor do cálculo progressivo está correto.
        assertEquals(valorEsperado.setScale(2, RoundingMode.HALF_UP), desconto);
        
        // Verifica se o contexto foi atualizado corretamente
        assertEquals(salarioBruto.subtract(valorEsperado).setScale(2, RoundingMode.HALF_UP), ctx.getSalarioBaseCalculo());
    }
}