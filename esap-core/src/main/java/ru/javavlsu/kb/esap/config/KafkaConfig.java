package ru.javavlsu.kb.esap.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${notification.kafka.topic.welcome-email}")
    private String welcomeEmailTopic;
    @Value("${notification.kafka.topic.push-notification}")
    private String pushNotificationTopic;
    @Value("${notification.kafka.topic.token-registration}")
    private String tokenRegistrationTopic;
    private final KafkaProperties kafkaProperties;

    @Autowired
    public KafkaConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> properties = kafkaProperties.buildProducerProperties();
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic welcomeEmailTopic() {
        return TopicBuilder
                .name(welcomeEmailTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic pushNotificationTopic() {
        return TopicBuilder
                .name(pushNotificationTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic tokenRegistrationTopic() {
        return TopicBuilder
                .name(tokenRegistrationTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
