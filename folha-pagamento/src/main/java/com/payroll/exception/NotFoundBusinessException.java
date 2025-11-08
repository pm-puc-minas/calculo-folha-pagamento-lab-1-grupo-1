package com.payroll.exception;
/*
 * Exceção para recursos não encontrados.
 * Usada quando entidade/registro não existe,
 * normalmente mapeada para HTTP 404 (Not Found).
 * Comunica ausência de recurso ao cliente de forma clara.
 */

import com.payroll.exception.base.AbstractBusinessException;

public class NotFoundBusinessException extends AbstractBusinessException {
    public NotFoundBusinessException(String message) {
        super(message);
    }
    public NotFoundBusinessException(String message, Object context) {
        super(message, context);
    }
}