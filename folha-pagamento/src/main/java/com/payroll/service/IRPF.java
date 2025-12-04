package com.payroll.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component; // Adicionar o import para a anotação de componente

@Component // Marca a classe como um componente Spring para injeção de dependência no PayrollService
// IRPF: imposto = (base * aliquota) - parcela_a_deduzir
public class IRPF implements IDesconto {

    @Override
    // Calcula IRPF conforme tabela (Base * Alíquota - Parcela a Deduzir)
    public BigDecimal calcular(SheetCalculator.DescontoContext ctx) {
        
        // Obtém a base de cálculo que já está atualizada pelo INSS e Pensão (se deduzida no Contexto)
        BigDecimal baseTributavel = ctx.getSalarioBaseCalculo(); 
        int dependentes = ctx.getDependentes();
        
        if (baseTributavel == null || baseTributavel.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        
        // Aplica a dedução por dependentes na base de cálculo
        BigDecimal deducaoDependentes = PayrollConstants.DEDUCAO_DEPENDENTE.multiply(new BigDecimal(dependentes));
        baseTributavel = baseTributavel.subtract(deducaoDependentes);

        // Se a base final, após todas as deduções, for menor ou igual à isenção.
        if (baseTributavel.compareTo(PayrollConstants.IRPF_ISENTO) <= 0) return BigDecimal.ZERO;
        
        // Retorna o indice da faixa do IRPF
        int faixaIndex = faixa(baseTributavel);
        // Alíquota da faixa
        BigDecimal aliquota = PayrollConstants.IRPF_RATES[faixaIndex];
        // Parcela a deduzir da faixa (essencial para o cálculo correto)
        BigDecimal parcelaADeduzir = PayrollConstants.IRPF_DEDUCTIONS[faixaIndex]; 

        // Cálculo final: Imposto = (Base * Alíquota) - Parcela a Deduzir
        BigDecimal imposto = baseTributavel.multiply(aliquota).subtract(parcelaADeduzir);

        // Garante que o imposto não seja negativo
        if (imposto.compareTo(BigDecimal.ZERO) < 0) imposto = BigDecimal.ZERO; 
        
        // Arredondamento
        imposto = imposto.setScale(2, RoundingMode.HALF_UP);
        
        // Atualiza a base de cálculo no Contexto subtraindo o imposto de renda
        ctx.aplicarDesconto(imposto);
        
        return imposto;
    }

    // Retorna o indice da faixa do IRPF
    private int faixa(BigDecimal base) {
        if (base.compareTo(PayrollConstants.IRPF_LIMITS[0]) <= 0) return 0;
        if (base.compareTo(PayrollConstants.IRPF_LIMITS[1]) <= 0) return 1;
        if (base.compareTo(PayrollConstants.IRPF_LIMITS[2]) <= 0) return 2;
        if (base.compareTo(PayrollConstants.IRPF_LIMITS[3]) <= 0) return 3;
        return 4;
    }

    @Override
    // Prioridade de execucao: 2 (deve vir apos o INSS, que tem prioridade 1)
    public int prioridade() { return 2; }
}