package com.payroll.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroll.model.Department;
import org.springframework.stereotype.Service;

@Service
public class DepartmentSerializationService extends AbstractJacksonSerializationService<Department> {
    public DepartmentSerializationService(ObjectMapper objectMapper) {
        super(objectMapper, Department.class);
    }
}