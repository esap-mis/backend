package ru.javavlsu.kb.notificationservice.kafka

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import ru.javavlsu.kb.notificationservice.service.EmailService
import ru.javavlsu.kb.notificationservice.service.NotificationService

@Component
class KafkaConsumer @Autowired constructor(
    private val objectMapper: ObjectMapper,
    private val emailService: EmailService,
    private val notificationService: NotificationService
) {

    @Throws(JsonProcessingException::class)
    @KafkaListener(topics = ["esap.users.mail"], groupId = "notification-service")
    fun consumeUserData(@Payload message: String,
                        @Header("type") type: String) {
        val messageType = MessageType.valueOf(type)
        if (messageType == MessageType.COMMAND) {
            val userData = objectMapper.readTree(message)

            val emailSubject = "Добро пожаловать в нашу клинику"
            val toAddress = userData.get("email").asText()
            val emailMessage = """
                Уважаемый ${userData.get("firstName").asText()}!
                Высылаем вам данные для личного кабинета
                Логин: ${userData.get("login").asText()}
                Пароль: ${userData.get("password").asText()}
                
                С уважением, поликлиника "${userData.get("clinic").get("name").asText()}".
                """.trimIndent()
            emailService.sendEmail(toAddress, emailSubject, emailMessage)
        }
    }

    @Throws(JsonProcessingException::class)
    @KafkaListener(topics = ["esap.users.notifications.mobile"], groupId = "notification-service")
    fun consumeUserNotification(@Payload message: String,
                                @Header("type") type: String) {
        val messageType = MessageType.valueOf(type)
        if (messageType == MessageType.COMMAND) {
            val notificationData = objectMapper.readTree(message)

            val to = notificationData.get("to").asText()
            val title = notificationData.get("title").asText()
            val body = notificationData.get("body").asText()

            notificationService.sendNotificationByToken(to, title, body)
        }
    }
}