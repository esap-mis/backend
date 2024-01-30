package ru.javavlsu.kb.esap.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @Column(name = "key")
    private String key;

    @Column(name = "size")
    private Long size;

    @Column(name = "upload_date")
    private LocalDate uploadDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private MedicalRecord medicalRecord;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return Objects.equals(id, document.id) && Objects.equals(fileName, document.fileName) && Objects.equals(fileType, document.fileType) && Objects.equals(key, document.key) && Objects.equals(size, document.size) && Objects.equals(uploadDate, document.uploadDate) && Objects.equals(medicalRecord, document.medicalRecord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fileName, fileType, key, size, uploadDate, medicalRecord);
    }
}

