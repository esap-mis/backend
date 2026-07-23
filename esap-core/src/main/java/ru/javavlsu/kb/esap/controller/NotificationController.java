package ru.javavlsu.kb.esap.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.javavlsu.kb.esap.dto.notifications.TokenRegistrationEvent;
import ru.javavlsu.kb.esap.dto.notifications.TokenRequest;
import ru.javavlsu.kb.esap.kafka.KafkaProducer;
import ru.javavlsu.kb.esap.model.User;
import ru.javavlsu.kb.esap.util.UserUtils;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private final UserUtils userUtils;
    private final KafkaProducer kafkaProducer;

    public NotificationController(UserUtils userUtils, KafkaProducer kafkaProducer) {
        this.userUtils = userUtils;
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/token")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public ResponseEntity<?> registerToken(@RequestBody TokenRequest request) throws JsonProcessingException {
        final User user = userUtils.UserDetails().getUser();
        final TokenRegistrationEvent registrationEvent = new TokenRegistrationEvent(user.getId(), request.token());
        kafkaProducer.sendTokenRegistrationEvent(registrationEvent);
        return ResponseEntity.ok("Token registration in progress");
    }
}
