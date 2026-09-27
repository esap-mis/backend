package ru.javavlsu.kb.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.javavlsu.kb.common.event.TokenRegistrationEvent;
import ru.javavlsu.kb.common.kafka.EventPublisher;
import ru.javavlsu.kb.common.security.CurrentUser;
import ru.javavlsu.kb.core.dto.TokenRequest;

/**
 * esap-core публикует событие регистрации токена — сам Mongo не хранит.
 */
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final CurrentUser currentUser;
    private final EventPublisher eventPublisher;

    @PostMapping("/token")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<?> registerToken(@RequestBody TokenRequest request) {
        eventPublisher.sendTokenRegistrationEvent(new TokenRegistrationEvent(currentUser.id(), request.token()));
        return ResponseEntity.ok("Token registration in progress");
    }
}
