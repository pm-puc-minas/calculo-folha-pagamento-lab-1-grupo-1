package com.payroll;

/*
 * Classe principal de inicialização da aplicação Spring Boot.
 * Responsável pelo bootstrap do framework, configuração automática
 * e inicialização do servidor web embutido para execução da API.
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FolhaPagamentoApplication {

    public static void main(String[] args) {
        // Iniciar a aplicação e subir o contexto do Spring
        SpringApplication.run(FolhaPagamentoApplication.class, args);
    }
}