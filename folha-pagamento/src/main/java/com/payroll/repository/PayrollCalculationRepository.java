package com.payroll.repository;

/*
 * Interface de repositório para a entidade PayrollCalculation.
 * Gerencia o acesso ao histórico de folhas de pagamento processadas,
 * permitindo consultas por funcionário, período de competência e estatísticas gerais.
 */

import com.payroll.entity.PayrollCalculation;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollCalculationRepository extends BaseRepository<PayrollCalculation, Long> {

    // Buscar todo o histórico de pagamentos de um funcionário específico
    List<PayrollCalculation> findByEmployeeId(Long employeeId);

    // Buscar uma folha específica (utilizado para evitar duplicidade de cálculo no mesmo mês)
    Optional<PayrollCalculation> findByEmployeeIdAndReferenceMonth(Long employeeId, String referenceMonth);

    // Recuperar a última folha processada no sistema (para exibir "Último processamento" no Dashboard)
    Optional<PayrollCalculation> findTopByOrderByCreatedAtDesc();

    // Contar quantas folhas foram geradas em um determinado mês (para estatísticas/dashboard)
    long countByReferenceMonth(String referenceMonth);
}