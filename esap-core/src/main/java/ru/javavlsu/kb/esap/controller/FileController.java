package ru.javavlsu.kb.esap.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javavlsu.kb.esap.dto.response.FileResponseDTO;
import ru.javavlsu.kb.esap.model.Document;
import ru.javavlsu.kb.esap.service.file.FileService;

import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("/api/file")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/{id}")
    public ResponseEntity<FileResponseDTO> upload(@PathVariable("id") Long recordId,
                                                  @RequestParam("attachment") MultipartFile attachment) throws IOException {
        Document document = fileService.upload(recordId, attachment);
        String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("api/file/download/" + document.getId())
                .toUriString();
        FileResponseDTO response = FileResponseDTO.builder()
                .fileName(document.getFileName())
                .downloadUrl(downloadUrl)
                .fileType(document.getType())
                .fileSize(document.getSize())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "download/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> download(@PathVariable("id") Long id) throws IOException {
        Document foundFile = fileService.findById(id);
        Resource resource = fileService.download(foundFile.getKey());
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + foundFile.getFileName())
                .body(resource);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        try {
            fileService.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
