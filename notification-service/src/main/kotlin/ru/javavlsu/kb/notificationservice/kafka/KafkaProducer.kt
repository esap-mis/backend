package ru.javavlsu.kb.notificationservice.kafka

import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaProducer constructor(
    @Value("\${notifications.topic.name}")
    private val notificationsTopic: String,
    private val kafkaTemplate: KafkaTemplate<String, String>
) {
    private val log = LoggerFactory.getLogger(KafkaProducer::class.java)

    fun sendTokenStatusMessage(token: String) {
        val message = "Token {token=$token} is inactive due to device unavailability"
        val record = ProducerRecord<String, String>(notificationsTopic, message)
        record.headers().add("type", MessageType.RESPONSE.name.toByteArray())
        kafkaTemplate.send(record)
        log.info(message)
    }
}