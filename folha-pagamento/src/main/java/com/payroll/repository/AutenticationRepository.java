package com.payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payroll.entity.User;

public interface AutenticationRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
