package ru.javavlsu.kb.esap.service.file;

import jakarta.transaction.Transactional;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import ru.javavlsu.kb.esap.exception.NotFoundException;
import ru.javavlsu.kb.esap.model.Document;
import ru.javavlsu.kb.esap.model.MedicalRecord;
import ru.javavlsu.kb.esap.repository.DocumentRepository;
import ru.javavlsu.kb.esap.repository.MedicalRecordRepository;
import ru.javavlsu.kb.esap.util.FileManager;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class FileServiceImpl implements FileService  {

    private final FileManager fileManager;
    private final DocumentRepository documentRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    @Autowired
    public FileServiceImpl(FileManager fileManager, DocumentRepository documentRepository, MedicalRecordRepository medicalRecordRepository) {
        this.fileManager = fileManager;
        this.documentRepository = documentRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @Override
    @Transactional(rollbackOn = IOException.class)
    public Document upload(Long recordId, MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String key = generateKey(fileName);
        try {
            if (fileName.contains("..")) {
                throw new IOException("Filename contains invalid path sequence " + fileName);
            }
            MedicalRecord medicalRecord = medicalRecordRepository.findById(recordId)
                    .orElseThrow(() -> new NotFoundException("Medical record not found"));
            String[] fileNameInfo = fileName.split("\\.");
            Document attachment = Document.builder()
                    .fileName(fileNameInfo[0])
                    .key(key)
                    .size(file.getSize())
                    .type(fileNameInfo[1])
                    .uploadDate(LocalDate.now())
                    .medicalRecord(medicalRecord)
                    .build();
            attachment = documentRepository.save(attachment);
            fileManager.upload(file.getBytes(), key);
            return attachment;
        } catch (MaxUploadSizeExceededException e) {
            throw new MaxUploadSizeExceededException(file.getSize());
        } catch (IOException e) {
            throw new IOException("Could not save File: " + fileName);
        }
    }

    @Override
    public Document findById(Long fileId) {
        return documentRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("Document not found"));
    }

    @Override
    public Resource download(String key) throws IOException {
        Resource resource = fileManager.download(key);
        Document file = documentRepository.findDocumentByKey(key)
                .orElseThrow(() -> new NotFoundException("Document not found"));

        String fileExtension = file.getType();

        File newFile = File.createTempFile(key, "." + fileExtension);
        try (InputStream inputStream = resource.getInputStream();
             OutputStream outputStream = new FileOutputStream(newFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return new FileSystemResource(newFile);
    }


    @Override
    public Resource download(String key, String fileType) throws IOException {
        Resource resource = fileManager.download(key);
        return resource;
    }

    @Override
    @Transactional(rollbackOn = IOException.class)
    public void delete(Long fileId) throws IOException {
        Document file = documentRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("Document not found"));
        documentRepository.deleteById(fileId);
        fileManager.delete(file.getKey());
    }

    private String generateKey(String name) {
        return DigestUtils.md5Hex(name + LocalDateTime.now());
    }
}