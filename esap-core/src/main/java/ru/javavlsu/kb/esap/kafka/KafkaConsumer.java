package ru.javavlsu.kb.esap.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.javavlsu.kb.esap.service.UserDeviceTokenService;

@Slf4j
@Component
public class KafkaConsumer {

    private final UserDeviceTokenService tokenService;

    public KafkaConsumer(UserDeviceTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @KafkaListener(topics = "esap.users.notifications.mobile", groupId = "esap-core")
    public void consumeNotificationServiceResponse(@Payload String message,
                                                   @Header("type") String type) {
        MessageType messageType = MessageType.valueOf(type);
        if (messageType == MessageType.RESPONSE) {
            String token = extractToken(message);
            tokenService.disableToken(token);
            log.info("Token status updated: " + token);
        }
    }

    private static String extractToken(String message) {
        int startIndex = message.indexOf("{token=") + "{token=".length();
        int endIndex = message.indexOf("}", startIndex);

        if (startIndex >= 0 && endIndex >= 0) {
            return message.substring(startIndex, endIndex);
        } else {
            return null;
        }
    }
}
