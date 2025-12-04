package com.payroll.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Armazena arquivos em disco (pasta temp) e mantém metadados em memória.
 */
@Service
public class FileStorageService {

    private final Path storageDir = Paths.get(System.getProperty("java.io.tmpdir"), "payroll-files");
    private final AtomicLong idSeq = new AtomicLong(1);
    private final Map<Long, StoredFile> files = new ConcurrentHashMap<>();

    public FileStorageService() throws IOException {
        Files.createDirectories(storageDir);
    }

    public StoredFile save(MultipartFile file) throws IOException {
        long id = idSeq.getAndIncrement();
        String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : ("file-" + id);
        Path target = storageDir.resolve(id + "-" + original);
        Files.copy(file.getInputStream(), target);
        StoredFile stored = new StoredFile(id, original, file.getContentType(), target.toAbsolutePath().toString());
        files.put(id, stored);
        return stored;
    }

    public Collection<StoredFile> list() {
        return files.values();
    }

    public StoredFile find(Long id) {
        return files.get(id);
    }

    public boolean delete(Long id) throws IOException {
        StoredFile stored = files.remove(id);
        if (stored != null) {
            Files.deleteIfExists(Path.of(stored.path()));
            return true;
        }
        return false;
    }

    public Resource loadAsResource(Long id) {
        StoredFile stored = files.get(id);
        if (stored == null) return null;
        return new FileSystemResource(stored.path());
    }

    public record StoredFile(Long id, String filename, String contentType, String path) {}
}
