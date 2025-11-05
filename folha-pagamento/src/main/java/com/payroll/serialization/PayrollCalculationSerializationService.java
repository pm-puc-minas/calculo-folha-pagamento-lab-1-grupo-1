package com.payroll.serialization;
/*
 * Serialização de cálculos de folha (PayrollCalculation).
 * Compõe valores de proventos, descontos e líquido,
 * padroniza campos para auditoria/relatórios e APIs.
 * JSON consistente de cálculos para consumo externo.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroll.entity.PayrollCalculation;
import org.springframework.stereotype.Service;

@Service
public class PayrollCalculationSerializationService extends AbstractJacksonSerializationService<PayrollCalculation> {
    public PayrollCalculationSerializationService(ObjectMapper objectMapper) {
        super(objectMapper, PayrollCalculation.class);
    }
}