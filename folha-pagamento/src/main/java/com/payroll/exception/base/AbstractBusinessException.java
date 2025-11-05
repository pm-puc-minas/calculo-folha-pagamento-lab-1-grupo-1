package com.payroll.exception.base;
/*
 * Classe abstrata para exceções de negócio.
 * Define estrutura comum (código, mensagem, causa)
 * e facilita especializações por tipo de erro.
 * Padroniza implementação de exceções específicas do domínio.
 */

public abstract class AbstractBusinessException extends RuntimeException implements BusinessException {
    private final Object context;

    public AbstractBusinessException(String message) {
        super(message);
        this.context = null;
    }

    public AbstractBusinessException(String message, Throwable cause) {
        super(message, cause);
        this.context = null;
    }

    public AbstractBusinessException(String message, Object context) {
        super(message);
        this.context = context;
    }

    public AbstractBusinessException(String message, Object context, Throwable cause) {
        super(message, cause);
        this.context = context;
    }

    @Override
    public Object getContext() {
        return context;
    }
}