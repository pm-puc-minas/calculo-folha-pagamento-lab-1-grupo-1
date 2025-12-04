package com.payroll.controller;

import com.payroll.service.FileStorageService;
import com.payroll.service.FileStorageService.StoredFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Endpoints de upload, listagem e download de arquivos para HistoryFilesPage.
 */
@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "http://localhost:5173") // front local
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<Collection<StoredFile>> listFiles() {
        return ResponseEntity.ok(fileStorageService.list());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            StoredFile stored = fileStorageService.save(file);
            return ResponseEntity.status(201).body(stored);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Falha ao salvar arquivo: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<?> download(@PathVariable Long id) {
        Resource resource = fileStorageService.loadAsResource(id);
        if (resource == null || !resource.exists()) {
            return ResponseEntity.status(404).body("Arquivo nao encontrado");
        }
        StoredFile meta = fileStorageService.find(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + meta.filename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            boolean removed = fileStorageService.delete(id);
            if (!removed) return ResponseEntity.status(404).body("Arquivo nao encontrado");
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Falha ao excluir arquivo: " + e.getMessage()));
        }
    }
}
