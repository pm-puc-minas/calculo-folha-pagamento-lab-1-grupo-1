package com.payroll.serialization;
/*
 * Serialização específica de Employee.
 * Formata/mascara campos sensíveis (cpf/rg) e normaliza nomes.
 * Estrutura respostas amigáveis para APIs/Frontend.
 * Produz JSON consistente de funcionários.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroll.entity.Employee;
import org.springframework.stereotype.Service;

@Service
public class EmployeeSerializationService extends AbstractJacksonSerializationService<Employee> {
    public EmployeeSerializationService(ObjectMapper objectMapper) {
        super(objectMapper, Employee.class);
    }
}