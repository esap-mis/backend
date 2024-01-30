package ru.javavlsu.kb.esap.model;

public enum FileType {
    DOC,
    DOCX,
    PDF;

    public static FileType getFromString(String stringType) {
        return switch (stringType.toLowerCase()) {
            case "doc" -> DOC;
            case "docx" -> DOCX;
            case "pdf" -> PDF;
            default -> throw new IllegalArgumentException("Unsupported file type: " + stringType);
        };
    }
}
