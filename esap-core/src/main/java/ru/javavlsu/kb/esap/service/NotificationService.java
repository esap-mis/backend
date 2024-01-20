package ru.javavlsu.kb.esap.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javavlsu.kb.esap.dto.notifications.NotificationMessage;
import ru.javavlsu.kb.esap.kafka.KafkaProducer;
import ru.javavlsu.kb.esap.model.TokenStatus;
import ru.javavlsu.kb.esap.model.User;
import ru.javavlsu.kb.esap.model.UserDeviceToken;
import ru.javavlsu.kb.esap.repository.UserDeviceTokenRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class NotificationService {

    private final KafkaProducer kafkaProducer;
    private final UserDeviceTokenRepository userDeviceTokenRepository;

    public NotificationService(KafkaProducer kafkaProducer, UserDeviceTokenRepository userDeviceTokenRepository) {
        this.kafkaProducer = kafkaProducer;
        this.userDeviceTokenRepository = userDeviceTokenRepository;
    }

    public void sendNotificationToUser(User user, NotificationMessage message) {
        List<UserDeviceToken> userDevices = userDeviceTokenRepository.getUserDeviceTokensByUser(user);
        for (var userDevice : userDevices) {
            if (userDevice.getStatus() == TokenStatus.ACTIVE) {
                message.setTo(userDevice.getToken());
                kafkaProducer.sendUserDeviceNotification(message);
            }
        }
    }
}
