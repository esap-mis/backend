package ru.javavlsu.kb.notificationservice.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javavlsu.kb.notificationservice.kafka.KafkaProducer;

@Service
public class NotificationService {

    private final FirebaseMessaging firebaseMessaging;
    private final KafkaProducer kafkaProducer;
    private final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    public NotificationService(FirebaseMessaging firebaseMessaging, KafkaProducer kafkaProducer) {
        this.firebaseMessaging = firebaseMessaging;
        this.kafkaProducer = kafkaProducer;
    }

    public void sendNotificationByToken(String to, String title, String body) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(to)
                .setNotification(notification)
                .build();

        try {
            firebaseMessaging.send(message);
            log.info("Push-notification sent successfully to device: {token=" + to + "}");
        } catch (FirebaseMessagingException e) {
            kafkaProducer.sendTokenStatusMessage(to);
            log.error("Error sending push-notification: {error=" + e.getMessage() + "}");
        }
    }
}
