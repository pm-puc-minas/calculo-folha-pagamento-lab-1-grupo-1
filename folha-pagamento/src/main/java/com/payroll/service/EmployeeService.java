package com.payroll.service;

import com.payroll.entity.Employee;
import com.payroll.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import com.payroll.collections.CollectionOps;
import com.payroll.collections.FilterSpec;
import com.payroll.collections.GroupBySpec;

@Service
public class EmployeeService implements IEmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
@Override
    public Employee createEmployee(Employee employee, Long createdBy) {
        employee.setCreatedBy(createdBy);
        return employeeRepository.save(employee);
    }
@Override
    public List<Employee> getAllEmployees() {
        List<Employee> all = employeeRepository.findAll();
        return all.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }
@Override
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }
@Override
    public Optional<Employee> getEmployeeByCpf(String cpf) {
        return employeeRepository.findByCpf(cpf);
    }
@Override
    public boolean existsByCpf(String cpf) {
        return employeeRepository.existsByCpf(cpf);
    }
@Override
    public Employee updateEmployee(Long id, Employee employeeDetails) {
        Employee employee = employeeRepository.findById(id).orElseThrow();
        employee.setFullName(employeeDetails.getFullName());
        employee.setCpf(employeeDetails.getCpf());
        employee.setRg(employeeDetails.getRg());
        employee.setPosition(employeeDetails.getPosition());
        employee.setAdmissionDate(employeeDetails.getAdmissionDate());
        employee.setSalary(employeeDetails.getSalary());
        employee.setWeeklyHours(employeeDetails.getWeeklyHours());
        employee.setTransportVoucher(employeeDetails.getTransportVoucher());
        employee.setMealVoucher(employeeDetails.getMealVoucher());
        employee.setMealVoucherValue(employeeDetails.getMealVoucherValue());
        employee.setDangerousWork(employeeDetails.getDangerousWork());
        employee.setDangerousPercentage(employeeDetails.getDangerousPercentage());
        employee.setUnhealthyWork(employeeDetails.getUnhealthyWork());
        employee.setUnhealthyLevel(employeeDetails.getUnhealthyLevel());
        return employeeRepository.save(employee);
    }
@Override
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
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
}
