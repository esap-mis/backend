package ru.javavlsu.kb.notificationservice.service

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.javavlsu.kb.notificationservice.dto.NotificationEvent
import ru.javavlsu.kb.notificationservice.model.TokenStatus
import ru.javavlsu.kb.notificationservice.model.UserDeviceToken

@Service
class NotificationService(
    val firebaseMessaging: FirebaseMessaging,
    val userDeviceTokenService: UserDeviceTokenService,
) {
    private val log = LoggerFactory.getLogger(NotificationService::class.java)

    fun sendNotificationToUser(notificationEvent: NotificationEvent) {
        log.info("Starting notification send. UserId: {}, Title: {}", notificationEvent.userId, notificationEvent.title)
        val userDevices: List<UserDeviceToken> = userDeviceTokenService.getUserDeviceTokensByUserId(notificationEvent.userId)
        log.debug("Found {} devices for user {}. Devices: {}", userDevices.size, notificationEvent.userId, userDevices.map { it.id })
        val activeDevices = userDevices.filter { it.status == TokenStatus.ACTIVE }
        activeDevices.forEach { userDevice ->
            log.debug("Sending notification to device {} for user {}", userDevice.id, notificationEvent.userId)
            sendNotificationByToken(userDevice.token, notificationEvent.title, notificationEvent.body)
        }
    }

    private fun sendNotificationByToken(to: String, title: String, body: String) {
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
            log.info("Push notification sent successfully to device: {token=$to}")
        } catch (e: FirebaseMessagingException) {
            userDeviceTokenService.disableToken(to)
            log.error("Error sending push notification: {error=${e.message}}")
        }
    }
}