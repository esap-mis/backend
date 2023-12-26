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

    public void sendPatientData(User user) throws JsonProcessingException {
        String message = objectMapper.writeValueAsString(user);
        ProducerRecord<String, String> record = new ProducerRecord<>(mailTopic, message);
        record.headers().add("type", MessageType.COMMAND.name().getBytes());
        kafkaTemplate.send(record);
        log.info("Send data for patient {id=" + user.getId() + "}");
    }

    public void sendUserDeviceNotification(NotificationMessage notification) throws JsonProcessingException {
        String message = objectMapper.writeValueAsString(notification);
        ProducerRecord<String, String> record = new ProducerRecord<>(notificationsTopic, message);
        record.headers().add("type", MessageType.COMMAND.name().getBytes());
        kafkaTemplate.send(record);
        log.info("Send notification to user device {token=" + notification.getTo() + "}");
    }
}
