package com.payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payroll.model.Position;

public interface  PositionRepository extends JpaRepository<Position, Long>{
}
