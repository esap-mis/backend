package ru.javavlsu.kb.common.security;

import java.util.Set;

/**
 * Аутентифицированный пользователь, извлечённый из JWT.
 * Не зависит от JPA: используется всеми сервисами как principal.
 */
public record AuthenticatedUser(
        Long id,
        String login,
        Set<String> roles,
        Long clinicId
) {

    public AuthenticatedUser {
        roles = roles == null ? Set.of() : Set.copyOf(roles);
    }

    public boolean hasRole(String role) {
        return roles.stream().anyMatch(r -> r.equalsIgnoreCase(role) || r.equalsIgnoreCase("ROLE_" + role));
    }

    public boolean isPatient() {
        return hasRole("PATIENT");
    }

    public boolean isDoctor() {
        return !isPatient();
    }
}
