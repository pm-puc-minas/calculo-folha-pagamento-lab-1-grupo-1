package com.payroll.service;

import java.math.BigDecimal;

/**
 * Classe central de constantes e tabelas usadas em cálculos de folha de pagamento.
 */
public class PayrollConstants {

    private PayrollConstants() {}

    // TABELA DE INSS 2024
    public static final BigDecimal[] INSS_LIMITS = {
        new BigDecimal("1412.00"),  // até 1.412,00 → 7,5%
        new BigDecimal("2666.68"),  // 1.412,01 até 2.666,68 → 9%
        new BigDecimal("4000.03"),  // 2.666,69 até 4.000,03 → 12%
        new BigDecimal("7786.02")   // 4.000,04 até 7.786,02 → 14%
    };

    public static final BigDecimal[] INSS_RATES = {
        new BigDecimal("0.075"),  // 7,5%
        new BigDecimal("0.09"),   // 9%
        new BigDecimal("0.12"),   // 12%
        new BigDecimal("0.14")    // 14%
    };

    // TABELA DE IRPF 2024
    public static final BigDecimal IRPF_ISENTO = new BigDecimal("2259.20");

    public static final BigDecimal[] IRPF_LIMITS = {
        new BigDecimal("2259.20"),
        new BigDecimal("2826.65"),
        new BigDecimal("3751.05"),
        new BigDecimal("4664.68")
    };

    public static final BigDecimal[] IRPF_RATES = {
        new BigDecimal("0.0"),     // Isento
        new BigDecimal("0.075"),   // 7,5%
        new BigDecimal("0.15"),    // 15%
        new BigDecimal("0.225"),   // 22,5%
        new BigDecimal("0.275")    // 27,5%
    };

    // VALORES FIXOS E TAXAS GERAIS
    public static final BigDecimal DEDUCAO_DEPENDENTE = new BigDecimal("189.59");
    public static final BigDecimal SALARIO_MINIMO = new BigDecimal("1412.00");
    

    public static final BigDecimal FGTS_RATE = new BigDecimal("0.08");
    public static final BigDecimal TRANSPORTE_RATE = new BigDecimal("0.06");
    public static final BigDecimal DANGER_RATE = new BigDecimal("0.30");

    public static final BigDecimal INSALUBRITY_LOW = new BigDecimal("0.10");
    public static final BigDecimal INSALUBRITY_MEDIUM = new BigDecimal("0.20");
    public static final BigDecimal INSALUBRITY_HIGH = new BigDecimal("0.40");

    public static final BigDecimal WEEKS_PER_MONTH = new BigDecimal("4.33");
}
