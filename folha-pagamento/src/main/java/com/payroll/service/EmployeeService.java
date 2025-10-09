package com.payroll.service;

import com.payroll.entity.Employee;
import com.payroll.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee createEmployee(Employee employee, Long createdBy) {
        employee.setCreatedBy(createdBy);
        return employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public Optional<Employee> getEmployeeByCpf(String cpf) {
        return employeeRepository.findByCpf(cpf);
    }

    public boolean existsByCpf(String cpf) {
        return employeeRepository.existsByCpf(cpf);
    }

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

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
}