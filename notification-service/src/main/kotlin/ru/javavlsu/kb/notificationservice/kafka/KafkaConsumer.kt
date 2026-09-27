package ru.javavlsu.kb.notificationservice.kafka

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import ru.javavlsu.kb.common.event.NotificationEvent
import ru.javavlsu.kb.common.event.TokenRegistrationEvent
import ru.javavlsu.kb.common.event.PatientCreatedEvent
import ru.javavlsu.kb.notificationservice.service.email.EmailService
import ru.javavlsu.kb.notificationservice.service.NotificationService
import ru.javavlsu.kb.notificationservice.service.UserDeviceTokenService
import ru.javavlsu.kb.notificationservice.service.email.EmailBuilder

@Component
class KafkaConsumer @Autowired constructor(
    val objectMapper: ObjectMapper,
    val emailService: EmailService,
    val notificationService: NotificationService,
    val userDeviceTokenService: UserDeviceTokenService,
    val emailBuilder: EmailBuilder
) {

    @Throws(JsonProcessingException::class)
    @KafkaListener(topics = ["${esap.kafka.topics.welcome-email}"], groupId = "notification-service")
    fun consumePatientCreatedEvent(@Payload message: String) {
        val patientCreatedEvent = objectMapper.readValue(message,
            PatientCreatedEvent::class.java)
        val emailContent = emailBuilder.build(patientCreatedEvent)
        emailService.sendEmail(emailContent)
    }

    @Throws(JsonProcessingException::class)
    @KafkaListener(topics = ["${esap.kafka.topics.token-registration}"], groupId = "notification-service")
    fun consumeTokenRegistrationEvent(@Payload message: String) {
        val tokenRegistrationEvent = objectMapper.readValue(message,
            TokenRegistrationEvent::class.java)
        userDeviceTokenService.saveToken(tokenRegistrationEvent)
    }

    @Throws(JsonProcessingException::class)
    @KafkaListener(topics = ["${esap.kafka.topics.push-notification}"], groupId = "notification-service")
    fun consumeNotificationEvent(@Payload message: String) {
        val notificationEvent = objectMapper.readValue(message,
            NotificationEvent::class.java)
        notificationService.sendNotificationToUser(notificationEvent)
    }
}