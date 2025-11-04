package com.payroll.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroll.entity.Employee;
import org.springframework.stereotype.Service;

@Service
public class EmployeeSerializationService extends AbstractJacksonSerializationService<Employee> {
    public EmployeeSerializationService(ObjectMapper objectMapper) {
        super(objectMapper, Employee.class);
    }
}