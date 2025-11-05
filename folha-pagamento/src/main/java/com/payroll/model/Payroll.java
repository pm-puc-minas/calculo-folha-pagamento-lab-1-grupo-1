package com.payroll.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;


@Entity
public class Payroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mes_referencia")
    private String mesReferencia;

    @Column(name = "salario_hora")
    private BigDecimal salarioHora;

    @Column(name = "total_proventos")
    private BigDecimal totalProventos;

    @Column(name = "total_descontos")
    private BigDecimal totalDescontos;

    @Column(name = "salario_liquido")
    private BigDecimal salarioLiquido;

    @Column(name = "valor_adicional_periculosidade")
    private BigDecimal valorAdicionalPericulosidade;

    @Column(name = "valor_adicional_insalubridade")
    private BigDecimal valorAdicionalInsalubridade;

    @Column(name = "valor_vale_alimentacao")
    private BigDecimal valorValeAlimentacao;

    @Column(name = "valor_desconto_vale_transporte")
    private BigDecimal valorDescontoValeTransporte;

    @Column(name = "valor_desconto_inss")
    private BigDecimal valorDescontoINSS;

    @Column(name = "valor_fgts")
    private BigDecimal valorFGTS;

    @Column(name = "valor_desconto_irrf")
    private BigDecimal valorDescontoIRRF;

    @Column(name = "base_calculo_inss")
    private BigDecimal baseCalculoINSS;

    @Column(name = "base_calculo_fgts")
    private BigDecimal baseCalculoFGTS;

    @Column(name = "base_calculo_irrf")
    private BigDecimal baseCalculoIRRF;

    @ElementCollection
    @CollectionTable(name = "payroll_descontos", joinColumns = @JoinColumn(name = "payroll_id"))
    @Column(name = "desconto")
    private List<BigDecimal> descontos = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "payroll_proventos", joinColumns = @JoinColumn(name = "payroll_id"))
    @Column(name = "provento")
    private List<BigDecimal> proventos = new ArrayList<>();
    // Construtor
    public Payroll() {
        this.descontos = new ArrayList<>();
        this.proventos = new ArrayList<>();
        this.totalProventos = BigDecimal.ZERO;
        this.totalDescontos = BigDecimal.ZERO;
        this.salarioLiquido = BigDecimal.ZERO;
    }

    public Payroll(String mesReferencia) {
        this();
        this.mesReferencia = mesReferencia;
    }

    public void calcular() {
        this.totalProventos = calcularTotalProventos();
        this.totalDescontos = calcularTotalDesconto();
        this.salarioLiquido = calcularSalarioLiquido();
    }

    public void adicionarDesconto(BigDecimal valor) {
        descontos.add(valor);
    }

    public void adicionarProvento(BigDecimal valor) {
        proventos.add(valor);
    }

    public BigDecimal calcularTotalDesconto() {
        BigDecimal total = BigDecimal.ZERO;
        if (valorDescontoINSS != null) total = total.add(valorDescontoINSS);
        if (valorDescontoIRRF != null) total = total.add(valorDescontoIRRF);
        if (valorDescontoValeTransporte != null) total = total.add(valorDescontoValeTransporte);

        for (BigDecimal desconto : descontos) {
            total = total.add(desconto);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularTotalProventos() {
        BigDecimal total = BigDecimal.ZERO;
        if (valorAdicionalPericulosidade != null) total = total.add(valorAdicionalPericulosidade);
        if (valorAdicionalInsalubridade != null) total = total.add(valorAdicionalInsalubridade);
        if (valorValeAlimentacao != null) total = total.add(valorValeAlimentacao);

        for (BigDecimal provento : proventos) {
            total = total.add(provento);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularSalarioLiquido() {
        BigDecimal bruto = totalProventos != null ? totalProventos : BigDecimal.ZERO;
        BigDecimal descontos = totalDescontos != null ? totalDescontos : BigDecimal.ZERO;
        return bruto.subtract(descontos).setScale(2, RoundingMode.HALF_UP);
    }

    // Getters e Setters
    
    
    
    public String getMesReferencia() {
        return mesReferencia;
    }

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<BigDecimal> getDescontos() {
		return descontos;
	}

	public void setDescontos(List<BigDecimal> descontos) {
		this.descontos = descontos;
	}

	public List<BigDecimal> getProventos() {
		return proventos;
	}

	public void setProventos(List<BigDecimal> proventos) {
		this.proventos = proventos;
	}

	public void setMesReferencia(String mesReferencia) {
        this.mesReferencia = mesReferencia;
    }

    public BigDecimal getSalarioHora() {
        return salarioHora;
    }

    public void setSalarioHora(BigDecimal salarioHora) {
        this.salarioHora = salarioHora;
    }

    public BigDecimal getTotalProventos() {
        return totalProventos;
    }

    public void setTotalProventos(BigDecimal totalProventos) {
        this.totalProventos = totalProventos;
    }

    public BigDecimal getTotalDescontos() {
        return totalDescontos;
    }

    public void setTotalDescontos(BigDecimal totalDescontos) {
        this.totalDescontos = totalDescontos;
    }

    public BigDecimal getSalarioLiquido() {
        return salarioLiquido;
    }

    public void setSalarioLiquido(BigDecimal salarioLiquido) {
        this.salarioLiquido = salarioLiquido;
    }

    public BigDecimal getValorAdicionalPericulosidade() {
        return valorAdicionalPericulosidade;
    }

    public void setValorAdicionalPericulosidade(BigDecimal valorAdicionalPericulosidade) {
        this.valorAdicionalPericulosidade = valorAdicionalPericulosidade;
    }

    public BigDecimal getValorAdicionalInsalubridade() {
        return valorAdicionalInsalubridade;
    }

    public void setValorAdicionalInsalubridade(BigDecimal valorAdicionalInsalubridade) {
        this.valorAdicionalInsalubridade = valorAdicionalInsalubridade;
    }

    public BigDecimal getValorValeAlimentacao() {
        return valorValeAlimentacao;
    }

    public void setValorValeAlimentacao(BigDecimal valorValeAlimentacao) {
        this.valorValeAlimentacao = valorValeAlimentacao;
    }

    public BigDecimal getValorDescontoValeTransporte() {
        return valorDescontoValeTransporte;
    }

    public void setValorDescontoValeTransporte(BigDecimal valorDescontoValeTransporte) {
        this.valorDescontoValeTransporte = valorDescontoValeTransporte;
    }

    public BigDecimal getValorDescontoINSS() {
        return valorDescontoINSS;
    }

    public void setValorDescontoINSS(BigDecimal valorDescontoINSS) {
        this.valorDescontoINSS = valorDescontoINSS;
    }

    public BigDecimal getValorFGTS() {
        return valorFGTS;
    }

    public void setValorFGTS(BigDecimal valorFGTS) {
        this.valorFGTS = valorFGTS;
    }

    public BigDecimal getValorDescontoIRRF() {
        return valorDescontoIRRF;
    }

    public void setValorDescontoIRRF(BigDecimal valorDescontoIRRF) {
        this.valorDescontoIRRF = valorDescontoIRRF;
    }

    public BigDecimal getBaseCalculoINSS() {
        return baseCalculoINSS;
    }

    public void setBaseCalculoINSS(BigDecimal baseCalculoINSS) {
        this.baseCalculoINSS = baseCalculoINSS;
    }

    public BigDecimal getBaseCalculoFGTS() {
        return baseCalculoFGTS;
    }

    public void setBaseCalculoFGTS(BigDecimal baseCalculoFGTS) {
        this.baseCalculoFGTS = baseCalculoFGTS;
    }

    public BigDecimal getBaseCalculoIRRF() {
        return baseCalculoIRRF;
    }

    public void setBaseCalculoIRRF(BigDecimal baseCalculoIRRF) {
        this.baseCalculoIRRF = baseCalculoIRRF;
    }
}