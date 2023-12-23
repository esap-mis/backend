package ru.javavlsu.kb.esap.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.javavlsu.kb.esap.dto.notifications.NotificationMessage;
import ru.javavlsu.kb.esap.dto.notifications.TokenRequest;
import ru.javavlsu.kb.esap.exception.NotFoundException;
import ru.javavlsu.kb.esap.model.User;
import ru.javavlsu.kb.esap.model.UserDeviceToken;
import ru.javavlsu.kb.esap.repository.UserDeviceTokenRepository;
import ru.javavlsu.kb.esap.repository.UserRepository;
import ru.javavlsu.kb.esap.util.KafkaProducer;

import java.time.LocalTime;
import java.util.List;

@Service
public class UserDeviceTokenService {

    private final KafkaProducer kafkaProducer;
    private final UserRepository userRepository;
    private final UserDeviceTokenRepository userDeviceTokenRepository;

    public UserDeviceTokenService(KafkaProducer kafkaProducer, UserRepository userRepository, UserDeviceTokenRepository userDeviceTokenRepository) {
        this.kafkaProducer = kafkaProducer;
        this.userRepository = userRepository;
        this.userDeviceTokenRepository = userDeviceTokenRepository;
    }

    public void saveToken(User user, TokenRequest request) {
        String tokenValue = request.getToken();

        if (!userDeviceTokenRepository.existsByTokenAndUser(tokenValue, user)) {
            UserDeviceToken deviceToken = new UserDeviceToken();
            deviceToken.setToken(tokenValue);
            deviceToken.setUser(user);
            userDeviceTokenRepository.save(deviceToken);
        }
    }

    @Scheduled(fixedDelay = 60000)
    public void sendTestNotifications() throws JsonProcessingException {
        List<User> users = userRepository.findAll();
        for (var user: users) {
            List<UserDeviceToken> userDevices = userDeviceTokenRepository.getUserDeviceTokensByUser(user);
            for (var userDevice : userDevices) {
                NotificationMessage messageToUserDevice = new NotificationMessage();
                messageToUserDevice.setTo(userDevice.getToken());
                messageToUserDevice.setTitle("ESAP");
                messageToUserDevice.setBody("Time " + LocalTime.now());

                kafkaProducer.sendUserDeviceNotification(messageToUserDevice);
            }
        }
    }
}
