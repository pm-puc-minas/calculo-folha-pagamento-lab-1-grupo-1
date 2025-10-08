package com.payroll.repository;

import com.payroll.entity.PayrollCalculation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollCalculationRepository extends JpaRepository<PayrollCalculation, Long> {
    List<PayrollCalculation> findByEmployeeId(Long employeeId);
    Optional<PayrollCalculation> findByEmployeeIdAndReferenceMonth(Long employeeId, String referenceMonth);
}
