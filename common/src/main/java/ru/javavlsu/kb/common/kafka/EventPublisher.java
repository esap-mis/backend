package ru.javavlsu.kb.common.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.javavlsu.kb.common.event.AppointmentCreatedEvent;
import ru.javavlsu.kb.common.event.NotificationEvent;
import ru.javavlsu.kb.common.event.PatientCreatedEvent;
import ru.javavlsu.kb.common.event.TokenRegistrationEvent;
import ru.javavlsu.kb.common.event.UserProfileEvent;
import ru.javavlsu.kb.common.security.EsapProperties;

/**
 * Единая точка публикации событий. Токены и payload'ы — по одному на сервис,
 * чтобы при добавлении сервиса контракт менялся в одном месте.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final EsapProperties esapProperties;
    @Qualifier("kafkaObjectMapper")
    private final ObjectMapper objectMapper;

    public void sendNotificationEvent(NotificationEvent event) {
        send(esapProperties.getKafka().getTopics().getPushNotification(), event.userId().toString(), event,
                "notification for user {}", event.userId());
    }

    public void sendTokenRegistrationEvent(TokenRegistrationEvent event) {
        send(esapProperties.getKafka().getTopics().getTokenRegistration(), event.userId().toString(), event,
                "token registration for user {}", event.userId());
    }

    public void sendPatientCreatedEvent(PatientCreatedEvent event) {
        send(esapProperties.getKafka().getTopics().getWelcomeEmail(), event.email(), event,
                "patient created {}", event.patientId());
    }

    public void sendUserProfileEvent(UserProfileEvent event) {
        send(esapProperties.getKafka().getTopics().getUserProfile(), String.valueOf(event.userId()), event,
                "user profile {} ({})", event.userId(), event.userType());
    }

    public void sendAppointmentCreatedEvent(AppointmentCreatedEvent event) {
        send(esapProperties.getKafka().getTopics().getAppointmentCreated(), String.valueOf(event.appointmentId()), event,
                "appointment created {}", event.appointmentId());
    }

    private void send(String topic, String key, Object payload, String logMessage, Object... args) {
        try {
            kafkaTemplate.send(topic, key, objectMapper.writeValueAsString(payload));
            log.info("Published to {}: " + logMessage, args.length > 0
                    ? prepend(topic, args)
                    : topic);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event for topic {}", topic, e);
            throw new IllegalStateException("Failed to serialize event for topic " + topic, e);
        }
    }

    private Object[] prepend(String topic, Object[] args) {
        Object[] result = new Object[args.length + 1];
        result[0] = topic;
        System.arraycopy(args, 0, result, 1, args.length);
        return result;
    }
}
