package ru.javavlsu.kb.notificationservice.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.kafka.autoconfigure.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

/**
 * KafkaConfig 21.07.2026 thewyolar
 * Copyright (c) 2026.
 */
@Configuration
class KafkaConfig @Autowired constructor(
    @Value("\${notification.kafka.topic.welcome-email}")
    private val welcomeEmailTopic: String,
    @Value("\${notification.kafka.topic.push-notification}")
    private val pushNotificationTopic: String,
    @Value("\${notification.kafka.topic.token-registration}")
    private val tokenRegistrationTopic: String,
    private val kafkaProperties: KafkaProperties
) {

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory())
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        val properties = kafkaProperties.buildProducerProperties()
        return DefaultKafkaProducerFactory(properties)
    }
    
    @Bean
    fun welcomeEmailTopic(): NewTopic {
        return TopicBuilder
            .name(welcomeEmailTopic)
            .partitions(1)
            .replicas(1)
            .build()
    }

    @Bean
    fun pushNotificationTopic(): NewTopic {
        return TopicBuilder
            .name(pushNotificationTopic)
            .partitions(1)
            .replicas(1)
            .build()
    }

    @Bean
    fun tokenRegistrationTopic(): NewTopic {
        return TopicBuilder
            .name(tokenRegistrationTopic)
            .partitions(1)
            .replicas(1)
            .build()
    }

    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper();
    }
}