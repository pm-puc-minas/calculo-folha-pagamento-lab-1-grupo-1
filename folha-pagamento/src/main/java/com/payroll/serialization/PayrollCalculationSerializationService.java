package com.payroll.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroll.entity.PayrollCalculation;
import org.springframework.stereotype.Service;

@Service
public class PayrollCalculationSerializationService extends AbstractJacksonSerializationService<PayrollCalculation> {
    public PayrollCalculationSerializationService(ObjectMapper objectMapper) {
        super(objectMapper, PayrollCalculation.class);
    }
}