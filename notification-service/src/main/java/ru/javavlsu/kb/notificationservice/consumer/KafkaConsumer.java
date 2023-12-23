package ru.javavlsu.kb.notificationservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.javavlsu.kb.notificationservice.service.EmailService;
import ru.javavlsu.kb.notificationservice.service.NotificationService;

@Component
public class KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @Autowired
    public KafkaConsumer(ObjectMapper objectMapper, EmailService emailService, NotificationService notificationService) {
        this.objectMapper = objectMapper;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "esap.users.mail")
    public void consumeUserData(String message) throws JsonProcessingException {
        JsonNode userData = objectMapper.readTree(message);

        String emailSubject = "Добро пожаловать в нашу клинику";
        String toAddress = userData.get("email").asText();
        String emailMessage = "Уважаемый " + userData.get("firstName").asText()
                + "!\nВысылаем вам данные для личного кабинета"
                + "\nЛогин: " + userData.get("login").asText() + "\nПароль: " + userData.get("password").asText()
                + "\n\nС уважением, поликлиника \"" + userData.get("clinic").get("name").asText() + "\".";

        emailService.sendEmail(toAddress, emailSubject, emailMessage);
    }

    @KafkaListener(topics = "esap.users.notifications.mobile")
    public void consumeUserNotification(String message) throws JsonProcessingException {
        JsonNode notificationData = objectMapper.readTree(message);

        String to = notificationData.get("to").asText();
        String title = notificationData.get("title").asText();
        String body = notificationData.get("body").asText();

        notificationService.sendNotificationByToken(to, title, body);
    }
}
