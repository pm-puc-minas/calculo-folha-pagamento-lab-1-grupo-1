package com.payroll.service;

/*
 * Serviço responsável pela gestão de funcionários.
 * Implementa as regras de negócio para cadastro, atualização, consulta e remoção (CRUD),
 * além de fornecer filtros e agrupamentos em memória para relatórios e validações.
 */

import com.payroll.entity.Employee;
import com.payroll.repository.EmployeeRepository;
import com.payroll.exception.DataIntegrityBusinessException;
import com.payroll.exception.DatabaseConnectionException;
import com.payroll.exception.NotFoundBusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import com.payroll.collections.CollectionOps;
import com.payroll.collections.GroupBySpec;

@Service
public class EmployeeService implements IEmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee createEmployee(Employee employee, Long createdBy) {
        try {
            // Vincular o ID do usuário responsável pela criação (Auditoria)
            employee.setCreatedBy(createdBy);
            return employeeRepository.save(employee);
        } catch (DataIntegrityViolationException e) {
            // Tratar erro de duplicidade (ex: CPF já existente)
            throw new DataIntegrityBusinessException("Funcionario com CPF ja cadastrado", e);
        } catch (DataAccessResourceFailureException e) {
            // Tratar falhas de comunicação com o banco de dados
            throw new DatabaseConnectionException("Falha de conexão ao criar funcionário", e);
        }
    }

    @Override
    public List<Employee> getAllEmployees() {
        try {
            // Recuperar lista completa e filtrar nulos para garantir integridade
            List<Employee> all = employeeRepository.findAll();
            return all.stream().filter(Objects::nonNull).collect(Collectors.toList());
        } catch (DataAccessResourceFailureException e) {
            throw new DatabaseConnectionException("Falha de conexão ao listar funcionários", e);
        }
    }

    @Override
    public Optional<Employee> getEmployeeById(Long id) {
        try {
            return employeeRepository.findById(id);
        } catch (DataAccessResourceFailureException e) {
            throw new DatabaseConnectionException("Falha de conexão ao buscar funcionário por ID", e);
        }
    }

    @Override
    public Optional<Employee> getEmployeeByCpf(String cpf) {
        try {
            return employeeRepository.findByCpf(cpf);
        } catch (DataAccessResourceFailureException e) {
            throw new DatabaseConnectionException("Falha de conexão ao buscar funcionário por CPF", e);
        }
    }

    @Override
    public boolean existsByCpf(String cpf) {
        try {
            return employeeRepository.existsByCpf(cpf);
        } catch (DataAccessResourceFailureException e) {
            throw new DatabaseConnectionException("Falha de conexão ao verificar CPF", e);
        }
    }

    @Override
    public Employee updateEmployee(Long id, Employee employeeDetails) {
        try {
            // Verificar existência antes de atualizar
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new NotFoundBusinessException("Funcionário não encontrado: " + id));

            // Atualização manual dos campos cadastrais e contratuais
            employee.setFullName(employeeDetails.getFullName());
            employee.setCpf(employeeDetails.getCpf());
            employee.setRg(employeeDetails.getRg());
            employee.setPosition(employeeDetails.getPosition());
            employee.setAdmissionDate(employeeDetails.getAdmissionDate());
            employee.setSalary(employeeDetails.getSalary());
            employee.setWeeklyHours(employeeDetails.getWeeklyHours());

            // Atualização de benefícios de transporte e alimentação
            employee.setTransportVoucher(employeeDetails.getTransportVoucher());
            employee.setTransportVoucherValue(employeeDetails.getTransportVoucherValue());
            employee.setMealVoucher(employeeDetails.getMealVoucher());
            employee.setMealVoucherValue(employeeDetails.getMealVoucherValue());

            // Atualização de adicionais de risco
            employee.setDangerousWork(employeeDetails.getDangerousWork());
            employee.setDangerousPercentage(employeeDetails.getDangerousPercentage());
            employee.setUnhealthyWork(employeeDetails.getUnhealthyWork());
            employee.setUnhealthyLevel(employeeDetails.getUnhealthyLevel());
            
            // Atualização de planos de saúde e bem-estar
            employee.setHealthPlan(employeeDetails.getHealthPlan());
            employee.setHealthPlanValue(employeeDetails.getHealthPlanValue());
            employee.setDentalPlan(employeeDetails.getDentalPlan());
            employee.setDentalPlanValue(employeeDetails.getDentalPlanValue());
            employee.setGym(employeeDetails.getGym());
            employee.setGymValue(employeeDetails.getGymValue());

            // Atualização de controle de jornada
            employee.setTimeBank(employeeDetails.getTimeBank());
            employee.setTimeBankHours(employeeDetails.getTimeBankHours());
            employee.setOvertimeEligible(employeeDetails.getOvertimeEligible());
            employee.setOvertimeHours(employeeDetails.getOvertimeHours());
            
            return employeeRepository.save(employee);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityBusinessException("Violação de integridade ao atualizar funcionário", e);
        } catch (DataAccessResourceFailureException e) {
            throw new DatabaseConnectionException("Falha de conexão ao atualizar funcionário", e);
        }
    }

    @Override
    public void deleteEmployee(Long id) {
        try {
            // Remover registro físico do banco de dados
            employeeRepository.deleteById(id);
        } catch (DataAccessResourceFailureException e) {
            throw new DatabaseConnectionException("Falha de conexão ao deletar funcionário", e);
        }
    }

    // Filtrar funcionários com salário base superior a um valor específico (lógica em memória)
    public List<Employee> filterEmployeesBySalaryMin(BigDecimal minSalary) {
        List<Employee> all = getAllEmployees();
        return CollectionOps.filter(all, e -> {
            BigDecimal s = e.getSalary();
            if (s == null) return false;
            return minSalary == null || s.compareTo(minSalary) >= 0;
        });
    }

    // Agrupar funcionários por cargo (Position) para visualização categorizada
    public Map<String, List<Employee>> groupEmployeesByPosition() {
        List<Employee> all = getAllEmployees();
        return CollectionOps.groupBy(all, new GroupBySpec<String, Employee>() {
            @Override
            public String key(Employee item) {
                return item.getPosition();
            }
        });
    }

    // Identificar funcionários com dados inconsistentes (salário ou carga horária inválidos)
    public List<Employee> findInvalidEmployees() {
        List<Employee> all = getAllEmployees();
        return CollectionOps.filter(all, e -> {
            BigDecimal salary = e.getSalary();
            Integer weeklyHours = e.getWeeklyHours();
            boolean nonPositiveSalary = salary == null || salary.compareTo(BigDecimal.ZERO) <= 0;
            boolean invalidHours = weeklyHours == null || weeklyHours < 1;
            return nonPositiveSalary || invalidHours;
        });
    }

    // Buscar funcionários por nome (busca parcial) ou retornar todos caso o termo seja vazio
    public List<Employee> searchEmployees(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllEmployees();
        }
        try {
            return employeeRepository.findByFullNameContainingIgnoreCase(query);
        } catch (DataAccessResourceFailureException e) {
            throw new DatabaseConnectionException("Falha de conexão ao buscar funcionários por nome", e);
        }
    }
}