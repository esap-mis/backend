package ru.javavlsu.kb.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Доступ к текущему пользователю из сервисного слоя.
 * Заменяет кросс-сервисный UserUtils с приведением к JPA-сущности.
 */
@Component
@RequiredArgsConstructor
public class CurrentUser {

    public AuthenticatedUser get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new IllegalStateException("No authenticated user in security context");
        }
        return user;
    }

    public Long id() {
        return get().id();
    }

    public Long clinicId() {
        return get().clinicId();
    }

    public boolean isPatient() {
        return get().isPatient();
    }
}
