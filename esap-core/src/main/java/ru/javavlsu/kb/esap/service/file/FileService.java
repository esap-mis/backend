package ru.javavlsu.kb.esap.service.file;


import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ru.javavlsu.kb.esap.model.Document;

import java.io.IOException;

public interface FileService {

    Document upload(Long recordId, MultipartFile resource) throws IOException;

    Document findById(Long fileId);

    Resource download(String key) throws IOException;

    void delete(Long fileId) throws IOException;
}