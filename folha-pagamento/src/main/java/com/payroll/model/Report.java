package com.payroll.model;

import java.math.BigDecimal;

public class Report {
    private Payroll payroll;

    // Construtores
    public Report() {}

    public Report(Payroll payroll) {
        this.payroll = payroll;
    }

    public void exibirDemonstrativo(Payroll payroll) {
        System.out.println("=== DEMONSTRATIVO DE PAGAMENTO ===");
        System.out.println("Mês de Referência: " + payroll.getMesReferencia());
        System.out.println();

        System.out.println("PROVENTOS:");
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

        System.out.println("DESCONTOS:");
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

        if (payroll.getValorFGTS() != null &&
                payroll.getValorFGTS().compareTo(BigDecimal.ZERO) > 0) {
            System.out.println("FGTS: R$ " + payroll.getValorFGTS());
            System.out.println();
        }

        System.out.println("SALÁRIO LÍQUIDO: R$ " + payroll.getSalarioLiquido());
        System.out.println("================================");
    }

    // Getters e Setters
    public Payroll getFolhaPagamento() {
        return payroll;
    }

    public void setFolhaPagamento(Payroll payroll) {
        this.payroll = payroll;
    }
}
