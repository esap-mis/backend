package ru.javavlsu.kb.esap.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.javavlsu.kb.esap.dto.notifications.NotificationMessage;
import ru.javavlsu.kb.esap.model.User;

@Slf4j
@Component
public class KafkaProducer {

    @Value("${mail.topic.name}")
    private String mailTopic;
    @Value("${notifications.topic.name}")
    private String notificationsTopic;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaProducer(ObjectMapper objectMapper, KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    private void sendMessageToKafka(Object data, String topic, String logMessage) {
        String message;
        try {
            message = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("Error parsing to JSON", e);
            throw new IllegalArgumentException("Error parsing to JSON", e);
        }

        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
        record.headers().add("type", MessageType.COMMAND.name().getBytes());
        kafkaTemplate.send(record);
        log.info(logMessage);
    }

    public void sendPatientData(User user) {
        String logMessage = "Send data for patient {id=" + user.getId() + "}";
        sendMessageToKafka(user, mailTopic, logMessage);
    }

    public void sendUserDeviceNotification(NotificationMessage notification) {
        String logMessage = "Send notification to user device {token=" + notification.getTo() + "}";
        sendMessageToKafka(notification, notificationsTopic, logMessage);
    }
}
