package ru.javavlsu.kb.esap.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FileResponseDTO {
    private String fileName;
    private String downloadUrl;
    private String fileType;
    private long fileSize;
}