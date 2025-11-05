package com.payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payroll.model.Department;

public interface  DepartamentRepository extends JpaRepository<Department, Long>{
}
