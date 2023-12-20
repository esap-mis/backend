package ru.javavlsu.kb.notificationservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.javavlsu.kb.notificationservice.service.EmailService;

@Component
public class KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    @Autowired
    public KafkaConsumer(ObjectMapper objectMapper, EmailService emailService) {
        this.objectMapper = objectMapper;
        this.emailService = emailService;
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
}
