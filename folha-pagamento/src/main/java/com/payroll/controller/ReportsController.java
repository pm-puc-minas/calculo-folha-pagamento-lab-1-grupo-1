package com.payroll.controller;

/*
 * Controlador REST para gerenciamento de relatórios do sistema.
 * Disponibiliza endpoints para consulta de histórico, solicitação de
 * novos relatórios e download dos arquivos gerados (PDF).
 */

import com.payroll.dtos.report.ReportRequestDTO;
import com.payroll.dtos.report.ReportResponseDTO;
import com.payroll.service.ReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    @Autowired
    private ReportsService reportsService;

    @GetMapping("/history")
    public ResponseEntity<List<ReportResponseDTO>> getHistory(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String referenceMonth,
            @RequestParam(required = false) String type) {
        // Consultar histórico de relatórios com filtros opcionais (funcionário, período, tipo)
        return ResponseEntity.ok(reportsService.getHistory(employeeId, referenceMonth, type));
    }

    @PostMapping({"", "/", "/create"})
    public ResponseEntity<ReportResponseDTO> createReport(
            @RequestBody ReportRequestDTO request,
            Authentication authentication) {
        // Capturar usuário logado para auditoria (pode ser nulo dependendo da configuração de segurança)
        String username = authentication != null ? authentication.getName() : null;

        // Validar parâmetros obrigatórios da requisição
        if (request.getEmployeeId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "employeeId é obrigatório");
        }

        String referenceMonth = request.getReferenceMonth();
        String type = request.getType();
        
        // Definir mês atual como padrão caso a referência não seja informada
        if (referenceMonth == null || referenceMonth.isBlank()) {
            referenceMonth = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        }

        // Definir tipo padrão (Folha de Pagamento) caso não informado
        if (type == null || type.isBlank()) {
            type = "PAYROLL";
        }

        // Processar a criação e persistência do relatório via serviço
        return ResponseEntity.ok(
                reportsService.createReportDto(request.getEmployeeId(), referenceMonth, type, username));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadReport(@PathVariable Long id) {
        try {
            // Gerar o conteúdo binário do PDF através do serviço
            byte[] content = reportsService.generateReportContent(id);
            
            // Configurar cabeçalhos de resposta para forçar o download do arquivo (attachment)
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(content);

        } catch (Exception e) {
            // Tratamento de erros críticos durante a geração do arquivo
            e.printStackTrace();
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Erro ao gerar relatorio: " + e.getMessage()).getBytes());
        }
    }

    @DeleteMapping({"/{id}", "/{id}/delete"})
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        // Remover permanentemente um relatório do histórico pelo ID
        reportsService.deleteReport(id);
        return ResponseEntity.ok().build();
    }
}