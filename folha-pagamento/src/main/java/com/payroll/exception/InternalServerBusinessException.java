package com.payroll.exception;
/*
 * Exceção genérica para erros internos não previstos.
 * Encapsula falhas inesperadas na camada de serviço,
 * pode ser mapeada para HTTP 500 (Internal Server Error).
 * Mantém resposta consistente em casos não tratados.
 */

import com.payroll.exception.base.AbstractBusinessException;

public class InternalServerBusinessException extends AbstractBusinessException {
    public InternalServerBusinessException(String message) {
        super(message);
    }
    public InternalServerBusinessException(String message, Throwable cause) {
        super(message, null, cause);
    }
}