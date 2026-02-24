package ru.javavlsu.kb.esap.model;

public enum RoleName {
    ROLE_ADMIN,
    ROLE_CHIEF_DOCTOR,
    ROLE_DOCTOR,
    ROLE_REGISTRANT,
    ROLE_LABORATORY,
    ROLE_PATIENT;

    public static RoleName from(String value) {
        try {
            return RoleName.valueOf("ROLE_" + value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown role: " + value);
        }
    }

    public String withoutPrefix() {
        return name().replace("ROLE_", "");
    }
}
