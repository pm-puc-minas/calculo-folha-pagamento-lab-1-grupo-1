package com.payroll.repository;

/*
 * Interface de repositório para a entidade Employee.
 * Responsável por todas as operações de banco de dados relacionadas a funcionários,
 * incluindo buscas específicas por CPF, nome e relatórios de admissão recente.
 */

import com.payroll.entity.Employee;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends BaseRepository<Employee, Long> {

    // Buscar funcionário pelo documento CPF (chave única de negócio)
    Optional<Employee> findByCpf(String cpf);

    // Verificar se já existe um funcionário cadastrado com este CPF (validação)
    boolean existsByCpf(String cpf);

    // Buscar funcionários pelo nome parcial, ignorando letras maiúsculas/minúsculas (Search bar)
    java.util.List<Employee> findByFullNameContainingIgnoreCase(String name);

    // Retornar as 5 admissões mais recentes (utilizado para widget de "Recentes" no Dashboard)
    java.util.List<Employee> findTop5ByOrderByAdmissionDateDesc();
}