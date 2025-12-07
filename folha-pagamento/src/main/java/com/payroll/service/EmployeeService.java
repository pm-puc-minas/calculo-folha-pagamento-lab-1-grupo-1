package com.payroll.service;

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
            employee.setCreatedBy(createdBy);
            return employeeRepository.save(employee);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityBusinessException("Funcionario com CPF ja cadastrado", e);
        } catch (DataAccessResourceFailureException e) {
            throw new DatabaseConnectionException("Falha de conexão ao criar funcionário", e);
        }
    }
@Override
    public List<Employee> getAllEmployees() {
        try {
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
            Employee employee = employeeRepository.findById(id).orElseThrow(() -> new NotFoundBusinessException("Funcionário não encontrado: " + id));
            employee.setFullName(employeeDetails.getFullName());
            employee.setCpf(employeeDetails.getCpf());
            employee.setRg(employeeDetails.getRg());
            employee.setPosition(employeeDetails.getPosition());
            employee.setAdmissionDate(employeeDetails.getAdmissionDate());
            employee.setSalary(employeeDetails.getSalary());
            employee.setWeeklyHours(employeeDetails.getWeeklyHours());
            employee.setTransportVoucher(employeeDetails.getTransportVoucher());
            employee.setTransportVoucherValue(employeeDetails.getTransportVoucherValue());
            employee.setMealVoucher(employeeDetails.getMealVoucher());
            employee.setMealVoucherValue(employeeDetails.getMealVoucherValue());
            employee.setDangerousWork(employeeDetails.getDangerousWork());
            employee.setDangerousPercentage(employeeDetails.getDangerousPercentage());
            employee.setUnhealthyWork(employeeDetails.getUnhealthyWork());
            employee.setUnhealthyLevel(employeeDetails.getUnhealthyLevel());
            
            employee.setHealthPlan(employeeDetails.getHealthPlan());
            employee.setHealthPlanValue(employeeDetails.getHealthPlanValue());
            employee.setDentalPlan(employeeDetails.getDentalPlan());
            employee.setDentalPlanValue(employeeDetails.getDentalPlanValue());
            employee.setGym(employeeDetails.getGym());
            employee.setGymValue(employeeDetails.getGymValue());
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
            employeeRepository.deleteById(id);
        } catch (DataAccessResourceFailureException e) {
            throw new DatabaseConnectionException("Falha de conexão ao deletar funcionário", e);
        }
    }

    public List<Employee> filterEmployeesBySalaryMin(BigDecimal minSalary) {
        List<Employee> all = getAllEmployees();
        return CollectionOps.filter(all, e -> {
            BigDecimal s = e.getSalary();
            if (s == null) return false;
            return minSalary == null || s.compareTo(minSalary) >= 0;
        });
    }

    public Map<String, List<Employee>> groupEmployeesByPosition() {
        List<Employee> all = getAllEmployees();
        return CollectionOps.groupBy(all, new GroupBySpec<String, Employee>() {
            @Override
            public String key(Employee item) {
                return item.getPosition();
            }
        });
    }

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
