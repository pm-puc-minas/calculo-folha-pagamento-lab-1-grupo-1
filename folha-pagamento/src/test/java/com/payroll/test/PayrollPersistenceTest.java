package com.payroll.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.payroll.model.Payroll;
import com.payroll.repository.PayrollRepository;

@DataJpaTest
public class PayrollPersistenceTest {

    @Autowired
    private PayrollRepository payrollRepository; // <-- repositorio correto

    @Test
    void testSalvarERecuperarPayroll() {
        Payroll payroll = new Payroll("11/2025");
        payroll.setSalarioHora(new BigDecimal("25.50"));
        payroll.setValorAdicionalPericulosidade(new BigDecimal("200.00"));
        payroll.setValorDescontoINSS(new BigDecimal("300.00"));

        payroll.adicionarProvento(new BigDecimal("500.00"));
        payroll.adicionarDesconto(new BigDecimal("50.00"));

        payroll.calcular();

        // Salvar no banco
        Payroll salvo = payrollRepository.save(payroll);
        assertThat(salvo.getId()).isNotNull();

        // Recuperar do banco
        Payroll recuperado = payrollRepository.findById(salvo.getId()).orElse(null);
        assertThat(recuperado).isNotNull();
        assertThat(recuperado.getMesReferencia()).isEqualTo("11/2025");
        assertThat(recuperado.getSalarioLiquido()).isEqualTo(payroll.getSalarioLiquido());

        System.out.println("Payroll salvo e recuperado com sucesso:");
        System.out.println("ID: " + recuperado.getId());
        System.out.println("Mês: " + recuperado.getMesReferencia());
        System.out.println("Salário líquido: " + recuperado.getSalarioLiquido());
    }
}
