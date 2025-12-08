package com.payroll.service;

/*
 * Serviço responsável pela geração e gestão de relatórios do sistema.
 * Utiliza a biblioteca iText (OpenPDF) para renderizar documentos PDF dinâmicos 
 * (como Holerites e Fichas Cadastrais), além de manter o histórico de auditoria.
 */

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.payroll.dtos.report.ReportResponseDTO;
import com.payroll.entity.Employee;
import com.payroll.entity.PayrollCalculation;
import com.payroll.entity.Report;
import com.payroll.entity.User;
import com.payroll.repository.EmployeeRepository;
import com.payroll.repository.PayrollCalculationRepository;
import com.payroll.repository.ReportRepository;
import com.payroll.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // Recuperar histórico de relatórios aplicando filtros de pesquisa
    public List<ReportResponseDTO> getHistory(Long employeeId, String referenceMonth, String type) {
        List<Report> reports = reportRepository.findAll();

        // Filtragem em memória (Stream API)
        return reports.stream()
                .filter(r -> employeeId == null || employeeId.equals(r.getEmployeeId()))
                .filter(r -> referenceMonth == null || referenceMonth.isBlank() ||
                        (r.getReferenceMonth() != null && referenceMonth.equalsIgnoreCase(r.getReferenceMonth())))
                .filter(r -> type == null || type.isBlank() ||
                        (r.getReportType() != null && type.equalsIgnoreCase(r.getReportType())))
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }

    // Método principal de geração: identifica o tipo e roteia para o gerador específico
    public byte[] generateReportContent(Long reportId) throws DocumentException {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));

        if ("payroll".equalsIgnoreCase(report.getReportType()) || "PAYROLL".equalsIgnoreCase(report.getReportType())) {
            return generatePayrollReport(reportId);
        } else if ("employee".equalsIgnoreCase(report.getReportType()) || "EMPLOYEE".equalsIgnoreCase(report.getReportType())) {
            return generateEmployeeReport(reportId);
        } else if ("summary".equalsIgnoreCase(report.getReportType())) {
            // Fallback: usa o layout de holerite para resumos por enquanto
            return generatePayrollReport(reportId);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown report type: " + report.getReportType());
        }
    }

    // Gerador de PDF para Holerite (Layout Financeiro Tabular)
    public byte[] generatePayrollReport(Long reportId) throws DocumentException {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));

        // Buscar os dados calculados correspondentes
        Optional<PayrollCalculation> calculationOpt = payrollRepository.findByEmployeeIdAndReferenceMonth(
                report.getEmployeeId(), report.getReferenceMonth());

        if (calculationOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Payroll calculation not found for this report");
        }
        PayrollCalculation calc = calculationOpt.get();
        Employee emp = calc.getEmployee();

        // Inicializar documento PDF em memória
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        // Cabeçalho do Documento
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Holerite de Pagamento", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        // Tabela de Informações Cadastrais
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.addCell(createCell("Funcionario: " + emp.getFullName(), true));
        infoTable.addCell(createCell("CPF: " + emp.getCpf(), false));
        infoTable.addCell(createCell("Cargo: " + emp.getPosition(), false));
        infoTable.addCell(createCell("Mes Referencia: " + calc.getReferenceMonth(), false));
        document.add(infoTable);
        document.add(Chunk.NEWLINE);

        // Tabela de Valores (Proventos e Descontos)
        PdfPTable calcTable = new PdfPTable(2);
        calcTable.setWidthPercentage(100);
        calcTable.addCell(createCell("Descricao", true));
        calcTable.addCell(createCell("Valor", true));

        // Preenchimento das linhas financeiras
        addRow(calcTable, "Salario Bruto", calc.getGrossSalary());
        addRow(calcTable, "INSS", calc.getInssDiscount().negate()); // Exibir negativo para descontos
        addRow(calcTable, "IRRF", calc.getIrpfDiscount().negate());
        addRow(calcTable, "Vale Transporte", calc.getTransportDiscount().negate());
        addRow(calcTable, "Vale Refeicao", calc.getMealVoucherValue().negate());

        // Adicionais condicionais (só exibe se tiver valor)
        if (calc.getDangerousBonus().compareTo(BigDecimal.ZERO) > 0)
            addRow(calcTable, "Adicional Periculosidade", calc.getDangerousBonus());
        if (calc.getUnhealthyBonus().compareTo(BigDecimal.ZERO) > 0)
            addRow(calcTable, "Adicional Insalubridade", calc.getUnhealthyBonus());
        if (calc.getOvertimeValue().compareTo(BigDecimal.ZERO) > 0)
            addRow(calcTable, "Horas Extras", calc.getOvertimeValue());

        // Benefícios condicionais
        if (calc.getHealthPlanDiscount().compareTo(BigDecimal.ZERO) > 0)
            addRow(calcTable, "Plano de Saude", calc.getHealthPlanDiscount().negate());
        if (calc.getDentalPlanDiscount().compareTo(BigDecimal.ZERO) > 0)
            addRow(calcTable, "Plano Odontologico", calc.getDentalPlanDiscount().negate());
        if (calc.getGymDiscount().compareTo(BigDecimal.ZERO) > 0)
            addRow(calcTable, "GymPass", calc.getGymDiscount().negate());

        document.add(calcTable);
        document.add(Chunk.NEWLINE);

        // Totalizador Líquido
        Paragraph total = new Paragraph("Salario Liquido: R$ " + calc.getNetSalary().toString(),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);

        // Rodapé
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        Paragraph signature = new Paragraph("__________________________________________________\nGerado automaticamente pelo RH Pro",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10));
        signature.setAlignment(Element.ALIGN_CENTER);
        document.add(signature);

        document.close();
        return out.toByteArray();
    }

    // Gerador de PDF para Ficha de Funcionário (Layout Informativo)
    public byte[] generateEmployeeReport(Long reportId) throws DocumentException {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));

        Employee emp = employeeRepository.findById(report.getEmployeeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Ficha do Funcionario", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        addInfoRow(table, "Nome Completo", emp.getFullName());
        addInfoRow(table, "CPF", emp.getCpf());
        addInfoRow(table, "RG", emp.getRg());
        addInfoRow(table, "Cargo", emp.getPosition());
        addInfoRow(table, "Salario Base", "R$ " + emp.getSalary());
        addInfoRow(table, "Data Admissao", emp.getAdmissionDate().toString());
        addInfoRow(table, "Carga Horaria", emp.getWeeklyHours() + "h semanais");
        addInfoRow(table, "Dependentes", String.valueOf(emp.getDependents()));

        document.add(table);
        document.close();
        return out.toByteArray();
    }

    // --- Métodos Auxiliares de Construção de PDF ---
    
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

    // --- Persistência e Auditoria de Relatórios ---

    // Sobrecarga para criação via nome de usuário
    public Report createReport(Long employeeId, String referenceMonth, String type, String username) {
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        User user = null;
        if (username != null && !username.isBlank()) {
            user = userRepository.findByUsername(username).orElse(null);
        }

        return saveReport(emp, referenceMonth, type, user);
    }

    // Sobrecarga para criação via ID de usuário
    public Report createReport(Long employeeId, String referenceMonth, String type, Long userId) {
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return saveReport(emp, referenceMonth, type, user);
    }

    // Wrappers para retornar DTOs diretamente
    public ReportResponseDTO createReportDto(Long employeeId, String referenceMonth, String type, String username) {
        return toResponseDTO(createReport(employeeId, referenceMonth, type, username));
    }

    public ReportResponseDTO createReportDto(Long employeeId, String referenceMonth, String type, Long userId) {
        return toResponseDTO(createReport(employeeId, referenceMonth, type, userId));
    }

    // Lógica central de salvamento no banco de dados
    private Report saveReport(Employee emp, String referenceMonth, String type, User user) {
        Report report = new Report();
        report.setEmployeeId(emp.getId());
        report.setEmployeeName(emp.getFullName());
        
        // Default para mês atual caso não informado
        if (referenceMonth == null || referenceMonth.isBlank()) {
            referenceMonth = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        
        // Default para tipo PAYROLL
        if (type == null || type.isBlank()) {
            type = "PAYROLL";
        }
        report.setReferenceMonth(referenceMonth);
        report.setReportType(type);
        report.setStatus("COMPLETED");
        report.setGeneratedBy(user);

        return reportRepository.save(report);
    }

    // Converter Entidade -> DTO
    private ReportResponseDTO toResponseDTO(Report report) {
        ReportResponseDTO dto = new ReportResponseDTO();
        dto.setId(report.getId());
        dto.setReportType(report.getReportType());
        dto.setEmployeeName(report.getEmployeeName());
        dto.setReferenceMonth(report.getReferenceMonth());
        dto.setGeneratedAt(report.getGeneratedAt());
        dto.setStatus(report.getStatus());

        if (report.getGeneratedBy() != null) {
            User u = report.getGeneratedBy();
            ReportResponseDTO.GeneratedByDTO generatedBy = new ReportResponseDTO.GeneratedByDTO(
                    u.getId(),
                    u.getUsername() != null ? u.getUsername() : u.getName()
            );
            generatedBy.setRole(u.getRole() != null ? u.getRole().name() : "User");
            dto.setGeneratedBy(generatedBy);
        }
        return dto;
    }
}