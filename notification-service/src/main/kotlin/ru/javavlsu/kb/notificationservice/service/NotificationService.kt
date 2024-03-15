package ru.javavlsu.kb.notificationservice.service

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.javavlsu.kb.notificationservice.kafka.KafkaProducer

@Service
class NotificationService(
    private val firebaseMessaging: FirebaseMessaging,
    private val kafkaProducer: KafkaProducer,
) {
    private val log = LoggerFactory.getLogger(NotificationService::class.java)

    fun sendNotificationByToken(to: String, title: String, body: String) {
        val notification = Notification.builder()
            .setTitle(title)
            .setBody(body)
            .build()

        val message = Message.builder()
            .setToken(to)
            .setNotification(notification)
            .build()

        try {
            firebaseMessaging.send(message)
            log.info("Push-notification sent successfully to device: {token=$to}")
        } catch (e: FirebaseMessagingException) {
            kafkaProducer.sendTokenStatusMessage(to)
            log.error("Error sending push-notification: {error=${e.message}}")
        }
    }
}