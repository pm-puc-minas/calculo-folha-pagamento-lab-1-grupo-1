package com.payroll.exception;
/*
 * Exceção para erros de validação de entrada.
 * Sinaliza campos obrigatórios ausentes/formato inválido,
 * normalmente mapeada para HTTP 400 (Bad Request).
 * Separa falhas de input das regras de negócio.
 */

import java.util.Map;
import com.payroll.exception.base.AbstractBusinessException;

public class InputValidationException extends AbstractBusinessException {
    public InputValidationException(String message) {
        super(message);
    }
    public InputValidationException(String message, Map<String, Object> context) {
        super(message, context);
    }
}