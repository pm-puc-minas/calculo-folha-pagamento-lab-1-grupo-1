package com.payroll.service;

import com.payroll.entity.Employee;
import com.payroll.entity.PayrollCalculation;
import com.payroll.entity.Report;
import com.payroll.entity.User;
import com.payroll.repository.EmployeeRepository;
import com.payroll.repository.PayrollCalculationRepository;
import com.payroll.repository.ReportRepository;
import com.payroll.repository.UserRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ReportsService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PayrollCalculationRepository payrollRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Report> getHistory(Long employeeId, String referenceMonth, String type) {
        // This is a simplified filter. ideally use Specifications or custom query
        if (employeeId != null && referenceMonth != null && type != null) {
            return reportRepository.findByEmployeeIdAndReferenceMonthAndReportType(employeeId, referenceMonth, type);
        }
        // For now, return all or basic filtering. In a real app, we'd implement dynamic query.
        // Let's assume the controller handles the combination logic or we just return all for now if params missing.
        return reportRepository.findAll();
    }
    
    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }

    public byte[] generateReportContent(Long reportId) throws DocumentException {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found"));
            
        if ("payroll".equalsIgnoreCase(report.getReportType()) || "PAYROLL".equalsIgnoreCase(report.getReportType())) {
            return generatePayrollReport(reportId);
        } else if ("employee".equalsIgnoreCase(report.getReportType()) || "EMPLOYEE".equalsIgnoreCase(report.getReportType())) {
            return generateEmployeeReport(reportId);
        } else {
            throw new RuntimeException("Unknown report type: " + report.getReportType());
        }
    }

    public byte[] generatePayrollReport(Long reportId) throws DocumentException {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found"));

        // Find the corresponding payroll calculation
        // Assuming we can link via employee and month. 
        // Ideally Report should have a direct link to PayrollCalculation, but let's use employee+month
        Optional<PayrollCalculation> calculationOpt = payrollRepository.findByEmployeeIdAndReferenceMonth(
            report.getEmployeeId(), report.getReferenceMonth());

        if (calculationOpt.isEmpty()) {
            throw new RuntimeException("Payroll calculation not found for this report");
        }
        PayrollCalculation calc = calculationOpt.get();
        Employee emp = calc.getEmployee();

        // PDF Generation using OpenPDF
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        // Title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Holerite de Pagamento", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        // Employee Info Table
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.addCell(createCell("Funcionário: " + emp.getFullName(), true));
        infoTable.addCell(createCell("CPF: " + emp.getCpf(), false));
        infoTable.addCell(createCell("Cargo: " + emp.getPosition(), false));
        infoTable.addCell(createCell("Mês Referência: " + calc.getReferenceMonth(), false));
        document.add(infoTable);
        document.add(Chunk.NEWLINE);

        // Calculation Table
        PdfPTable calcTable = new PdfPTable(2);
        calcTable.setWidthPercentage(100);
        calcTable.addCell(createCell("Descrição", true));
        calcTable.addCell(createCell("Valor", true));

        addRow(calcTable, "Salário Bruto", calc.getGrossSalary());
        addRow(calcTable, "INSS", calc.getInssDiscount().negate()); // Show as negative
        addRow(calcTable, "IRRF", calc.getIrpfDiscount().negate());
        addRow(calcTable, "Vale Transporte", calc.getTransportDiscount().negate());
        addRow(calcTable, "Vale Refeição", calc.getMealVoucherValue().negate()); // Is this discount or bonus? Usually bonus but some copart. 
        // Assuming value stored is the cost/benefit. Logic usually subtracts discounts.
        // Let's assume standard deduction logic. 
        
        // Bonuses
        if (calc.getDangerousBonus().compareTo(BigDecimal.ZERO) > 0) 
            addRow(calcTable, "Adicional Periculosidade", calc.getDangerousBonus());
        if (calc.getUnhealthyBonus().compareTo(BigDecimal.ZERO) > 0) 
            addRow(calcTable, "Adicional Insalubridade", calc.getUnhealthyBonus());
        if (calc.getOvertimeValue().compareTo(BigDecimal.ZERO) > 0) 
            addRow(calcTable, "Horas Extras", calc.getOvertimeValue());

        // Other Discounts
        if (calc.getHealthPlanDiscount().compareTo(BigDecimal.ZERO) > 0)
            addRow(calcTable, "Plano de Saúde", calc.getHealthPlanDiscount().negate());
        if (calc.getDentalPlanDiscount().compareTo(BigDecimal.ZERO) > 0)
            addRow(calcTable, "Plano Odontológico", calc.getDentalPlanDiscount().negate());
        if (calc.getGymDiscount().compareTo(BigDecimal.ZERO) > 0)
            addRow(calcTable, "GymPass", calc.getGymDiscount().negate());

        document.add(calcTable);
        document.add(Chunk.NEWLINE);

        // Totals
        Paragraph total = new Paragraph("Salário Líquido: R$ " + calc.getNetSalary().toString(), 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);
        
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        
        // Signature
        Paragraph signature = new Paragraph("__________________________________________________\nGerado automaticamente pelo RH Pro",
            FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10));
        signature.setAlignment(Element.ALIGN_CENTER);
        document.add(signature);

        document.close();
        return out.toByteArray();
    }

    public byte[] generateEmployeeReport(Long reportId) throws DocumentException {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found"));
            
        Employee emp = employeeRepository.findById(report.getEmployeeId())
            .orElseThrow(() -> new RuntimeException("Employee not found"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Ficha do Funcionário", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        
        addInfoRow(table, "Nome Completo", emp.getFullName());
        addInfoRow(table, "CPF", emp.getCpf());
        addInfoRow(table, "RG", emp.getRg());
        addInfoRow(table, "Cargo", emp.getPosition());
        addInfoRow(table, "Salário Base", "R$ " + emp.getSalary());
        addInfoRow(table, "Data Admissão", emp.getAdmissionDate().toString());
        addInfoRow(table, "Carga Horária", emp.getWeeklyHours() + "h semanais");
        addInfoRow(table, "Dependentes", String.valueOf(emp.getDependents()));
        
        document.add(table);
        document.close();
        return out.toByteArray();
    }
    
    // Helper methods
    private PdfPCell createCell(String text, boolean bold) {
        Font font = bold ? FontFactory.getFont(FontFactory.HELVETICA_BOLD) : FontFactory.getFont(FontFactory.HELVETICA);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        return cell;
    }

    private void addRow(PdfPTable table, String label, BigDecimal value) {
        table.addCell(createCell(label, false));
        table.addCell(createCell("R$ " + value.toString(), false));
    }
    
    private void addInfoRow(PdfPTable table, String label, String value) {
        table.addCell(createCell(label, true));
        table.addCell(createCell(value != null ? value : "-", false));
    }

    public Report createReport(Long employeeId, String referenceMonth, String type, String username) {
        Employee emp = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
            
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return saveReport(emp, referenceMonth, type, user);
    }

    public Report createReport(Long employeeId, String referenceMonth, String type, Long userId) {
        Employee emp = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
            
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return saveReport(emp, referenceMonth, type, user);
    }

    private Report saveReport(Employee emp, String referenceMonth, String type, User user) {
        Report report = new Report();
        report.setEmployeeId(emp.getId());
        report.setEmployeeName(emp.getFullName());
        report.setReferenceMonth(referenceMonth);
        report.setReportType(type);
        report.setStatus("COMPLETED");
        report.setGeneratedBy(user);
        
        return reportRepository.save(report);
    }
}
