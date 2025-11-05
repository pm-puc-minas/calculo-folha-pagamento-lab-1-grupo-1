package com.payroll.exception.base;
/*
 * Exceção base de negócio (checked ou runtime conforme projeto).
 * Carrega código/mensagem padronizados e integra com tratador global,
 * facilitando categorização de erros de domínio.
 * Representa falhas de regras de negócio na camada de serviço.
 */

public interface BusinessException {
    String getMessage();
    Object getContext();
}