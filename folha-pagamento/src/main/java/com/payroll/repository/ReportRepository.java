package com.payroll.repository;

/*
 * Interface de repositório para a entidade Report.
 * Gerencia o acesso ao histórico de relatórios gerados (PDFs),
 * permitindo consultas filtradas por funcionário, período e tipo de documento.
 */

import com.payroll.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    // Buscar relatórios aplicando filtros compostos (Funcionário + Mês + Tipo) para recuperação exata
    List<Report> findByEmployeeIdAndReferenceMonthAndReportType(Long employeeId, String referenceMonth, String reportType);

    // Listar todo o histórico de relatórios gerados para um funcionário específico
    List<Report> findByEmployeeId(Long employeeId);
}