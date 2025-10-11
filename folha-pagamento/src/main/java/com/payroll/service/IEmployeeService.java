package com.payroll.service;

import com.payroll.entity.Employee;
import java.util.List;
import java.util.Optional;

/**
 * Interface para serviço de funcionários
 */
public interface IEmployeeService {
    
    /**
     * Cria novo funcionário
     * @param employee Dados do funcionário
     * @param createdBy ID do usuário que criou
     * @return Funcionário criado
     */
    Employee createEmployee(Employee employee, Long createdBy);
    
    /**
     * Lista todos os funcionários
     * @return Lista de funcionários
     */
    List<Employee> getAllEmployees();
    
    /**
     * Busca funcionário por ID
     * @param id ID do funcionário
     * @return Optional com funcionário
     */
    Optional<Employee> getEmployeeById(Long id);
    
    /**
     * Busca funcionário por CPF
     * @param cpf CPF do funcionário
     * @return Optional com funcionário
     */
    Optional<Employee> getEmployeeByCpf(String cpf);
    
    /**
     * Verifica se CPF já existe
     * @param cpf CPF a verificar
     * @return true se existe
     */
    boolean existsByCpf(String cpf);
    
    /**
     * Atualiza funcionário
     * @param id ID do funcionário
     * @param employeeDetails Dados atualizados
     * @return Funcionário atualizado
     */
    Employee updateEmployee(Long id, Employee employeeDetails);
    
    /**
     * Deleta funcionário
     * @param id ID do funcionário
     */
    void deleteEmployee(Long id);
}