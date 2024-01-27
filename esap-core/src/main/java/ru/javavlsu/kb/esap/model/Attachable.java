package ru.javavlsu.kb.esap.model;

import java.util.List;

@FunctionalInterface
public interface Attachable {
    List<Document> getDocuments();
}
