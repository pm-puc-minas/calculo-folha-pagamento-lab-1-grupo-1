package com.payroll.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component; // Adicionar o import para a anotação de componente

@Component // Marca a classe como um componente Spring para injeção de dependência no PayrollService
// IRRF: base = (base_atualizada_pelo_INSS) - (dependentes*valor_dedução) - (pensao); imposto = base * aliquota - parcela_a_deduzir
public class IRRF implements IDesconto {

    @Override
    // Calcula IRRF conforme tabela (agora com Parcela a Deduzir, método mais preciso)
    public BigDecimal calcular(SheetCalculator.DescontoContext ctx) {
        
        // 1. Obtem a base de cálculo atualizada (Salário Bruto - Pensão - INSS - Outros Descontos)
        BigDecimal baseTributavel = ctx.getSalarioBaseCalculo(); 
        int dependentes = ctx.getDependentes();
        
        if (baseTributavel == null || baseTributavel.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;

        // 2. Aplica a dedução por dependentes na base
        BigDecimal deducaoDependentes = PayrollConstants.DEDUCAO_DEPENDENTE.multiply(new BigDecimal(dependentes));
        baseTributavel = baseTributavel.subtract(deducaoDependentes);

        // 3. Verifica se a base final está na faixa de isenção
        if (baseTributavel.compareTo(PayrollConstants.IRPF_LIMITS[0]) <= 0) {
            return BigDecimal.ZERO;
        }

        // 4. Determina a faixa de imposto e a parcela a deduzir
        int faixaIndex = faixa(baseTributavel);
        BigDecimal aliquota = PayrollConstants.IRPF_RATES[faixaIndex];
        // Adiciona a dedução do IRRF, que é o método correto (Faixa X Alíquota - Parcela a Deduzir)
        BigDecimal parcelaADeduzir = PayrollConstants.IRPF_DEDUCTIONS[faixaIndex]; 
        
        // 5. Cálculo final: Imposto = (Base * Alíquota) - Parcela a Deduzir
        BigDecimal imposto = baseTributavel.multiply(aliquota).subtract(parcelaADeduzir);
        
        if (imposto.compareTo(BigDecimal.ZERO) < 0) imposto = BigDecimal.ZERO; // Garante que não será negativo
        
        imposto = imposto.setScale(2, RoundingMode.HALF_UP);
        
        // Atualiza a base de cálculo no Contexto para eventuais cálculos futuros
        ctx.aplicarDesconto(imposto); 
        
        return imposto;
    }

    // Retorna o indice da faixa do IR pela tabela
    private int faixa(BigDecimal base) {
        if (base.compareTo(PayrollConstants.IRPF_LIMITS[0]) <= 0) return 0;
        if (base.compareTo(PayrollConstants.IRPF_LIMITS[1]) <= 0) return 1;
        if (base.compareTo(PayrollConstants.IRPF_LIMITS[2]) <= 0) return 2;
        if (base.compareTo(PayrollConstants.IRPF_LIMITS[3]) <= 0) return 3;
        return 4;
    }

    @Override
    // Prioridade de execucao: 2 (apos INSS)
    public int prioridade() { return 2; }
}