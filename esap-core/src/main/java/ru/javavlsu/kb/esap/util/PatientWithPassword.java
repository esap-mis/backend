package ru.javavlsu.kb.esap.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.javavlsu.kb.esap.model.Patient;

@Getter
@Setter
@AllArgsConstructor
public class PatientWithPassword {
    private final Patient patient;
    private final String decryptedPassword;
}

