package ru.javavlsu.kb.esap.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.javavlsu.kb.esap.dto.notifications.NotificationEvent;
import ru.javavlsu.kb.esap.dto.notifications.TokenRegistrationEvent;
import ru.javavlsu.kb.esap.dto.notifications.PatientCreatedEvent;

@Slf4j
@Component
public class KafkaProducer {

    @Value("${notification.kafka.topic.welcome-email}")
    private String welcomeEmailTopic;
    @Value("${notification.kafka.topic.push-notification}")
    private String pushNotificationTopic;
    @Value("${notification.kafka.topic.token-registration}")
    private String tokenRegistrationTopic;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaProducer(ObjectMapper objectMapper, KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTokenRegistrationEvent(TokenRegistrationEvent tokenRegistrationEvent) throws JsonProcessingException {
        kafkaTemplate.send(
                tokenRegistrationTopic,
                tokenRegistrationEvent.userId().toString(),
                objectMapper.writeValueAsString(tokenRegistrationEvent)
        );
        log.info("Send token registration event {token={}}", tokenRegistrationEvent.token());
    }

    public void sendPatientCreatedEvent(PatientCreatedEvent patientCreatedEvent) throws JsonProcessingException {
        kafkaTemplate.send(
                welcomeEmailTopic,
                patientCreatedEvent.email(),
                objectMapper.writeValueAsString(patientCreatedEvent)
        );
        log.info("Sending patient created event to Kafka. Topic: {}, Email: {}",
                welcomeEmailTopic, patientCreatedEvent.email());
    }

    public void sendNotificationEvent(NotificationEvent notificationEvent) throws JsonProcessingException {
        kafkaTemplate.send(
                pushNotificationTopic,
                notificationEvent.userId().toString(),
                objectMapper.writeValueAsString(notificationEvent)
        );
        log.info("Send notification to user {id={}}", notificationEvent.userId());
    }
}
