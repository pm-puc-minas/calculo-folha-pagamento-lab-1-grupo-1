package com.payroll.repository;

import com.payroll.entity.Employee;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends BaseRepository<Employee, Long> {
    Optional<Employee> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
}
