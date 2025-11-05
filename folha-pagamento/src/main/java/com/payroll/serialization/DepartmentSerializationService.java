package com.payroll.serialization;
/*
 * Serialização de dados de Departamento.
 * Estrutura campos para dashboards/relatórios
 * e garante consistência de nomenclatura e formatos.
 * Expõe departamentos em JSON padronizado para o frontend.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroll.model.Department;
import org.springframework.stereotype.Service;

@Service
public class DepartmentSerializationService extends AbstractJacksonSerializationService<Department> {
    public DepartmentSerializationService(ObjectMapper objectMapper) {
        super(objectMapper, Department.class);
    }
}