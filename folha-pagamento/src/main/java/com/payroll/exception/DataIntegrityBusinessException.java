package com.payroll.exception;
/*
 * Exceção para violações de integridade de dados (ex.: unicidade).
 * Usada quando regras de persistência/consistência são quebradas,
 * mapeada normalmente para HTTP 409 (Conflict).
 * Sinaliza conflitos de dados na camada de serviço/repositório.
 */

public class DataIntegrityBusinessException extends RuntimeException {
    public DataIntegrityBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}