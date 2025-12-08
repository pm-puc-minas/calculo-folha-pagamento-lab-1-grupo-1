package com.payroll.model;

/*
 * Classe de visualização para o demonstrativo de pagamento (Holerite).
 * Responsável por formatar e exibir os dados calculados da folha no console,
 * organizando proventos, descontos e totais para fácil conferência visual.
 */

import java.math.BigDecimal;

public class Report {
    
    private Payroll payroll;

    // Construtores
    public Report() {}

    public Report(Payroll payroll) {
        this.payroll = payroll;
    }

    // Gera a saída formatada no console com os detalhes do pagamento
    public void exibirDemonstrativo(Payroll payroll) {
        // Cabeçalho e identificação
        System.out.println("=== DEMONSTRATIVO DE PAGAMENTO ===");
        System.out.println("Mês de Referência: " + payroll.getMesReferencia());
        System.out.println();

        // --- Seção de Proventos (Ganhos) ---
        System.out.println("PROVENTOS:");
        
        // Verifica e exibe Adicionais apenas se houver valor positivo
        if (payroll.getValorAdicionalPericulosidade() != null &&
                payroll.getValorAdicionalPericulosidade().compareTo(BigDecimal.ZERO) > 0) {
            System.out.println("  Adicional Periculosidade: R$ " + payroll.getValorAdicionalPericulosidade());
        }
        if (payroll.getValorAdicionalInsalubridade() != null &&
                payroll.getValorAdicionalInsalubridade().compareTo(BigDecimal.ZERO) > 0) {
            System.out.println("  Adicional Insalubridade: R$ " + payroll.getValorAdicionalInsalubridade());
        }
        if (payroll.getValorValeAlimentacao() != null &&
                payroll.getValorValeAlimentacao().compareTo(BigDecimal.ZERO) > 0) {
            System.out.println("  Vale Alimentação: R$ " + payroll.getValorValeAlimentacao());
        }
        System.out.println("  TOTAL PROVENTOS: R$ " + payroll.getTotalProventos());
        System.out.println();

        // --- Seção de Descontos ---
        System.out.println("DESCONTOS:");
        
        // Verifica e exibe descontos legais e de transporte
        if (payroll.getValorDescontoINSS() != null &&
                payroll.getValorDescontoINSS().compareTo(BigDecimal.ZERO) > 0) {
            System.out.println("  INSS: R$ " + payroll.getValorDescontoINSS());
        }
        if (payroll.getValorDescontoIRRF() != null &&
                payroll.getValorDescontoIRRF().compareTo(BigDecimal.ZERO) > 0) {
            System.out.println("  IRRF: R$ " + payroll.getValorDescontoIRRF());
        }
        if (payroll.getValorDescontoValeTransporte() != null &&
                payroll.getValorDescontoValeTransporte().compareTo(BigDecimal.ZERO) > 0) {
            System.out.println("  Vale Transporte: R$ " + payroll.getValorDescontoValeTransporte());
        }
        System.out.println("  TOTAL DESCONTOS: R$ " + payroll.getTotalDescontos());
        System.out.println();

        // --- Informativos (Não afetam o líquido diretamente) ---
        if (payroll.getValorFGTS() != null &&
                payroll.getValorFGTS().compareTo(BigDecimal.ZERO) > 0) {
            System.out.println("FGTS: R$ " + payroll.getValorFGTS());
            System.out.println();
        }

        // Resultado Final
        System.out.println("SALÁRIO LÍQUIDO: R$ " + payroll.getSalarioLiquido());
        System.out.println("================================");
    }

    // --- Getters e Setters ---
    
    public Payroll getFolhaPagamento() {
        return payroll;
    }

    public void setFolhaPagamento(Payroll payroll) {
        this.payroll = payroll;
    }
}