package com.payroll.exception;
/*
 * Exceção para falhas de conexão com banco de dados.
 * Captura indisponibilidade de recurso (timeouts, down),
 * permite resposta adequada e logs de infraestrutura.
 * Diferencia erros técnicos dos erros de negócio.
 */

public class DatabaseConnectionException extends RuntimeException {
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}