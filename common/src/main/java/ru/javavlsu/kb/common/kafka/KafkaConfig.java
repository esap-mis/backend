package ru.javavlsu.kb.common.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ru.javavlsu.kb.common.security.EsapProperties;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;
    private final EsapProperties esapProperties;

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
    public ObjectMapper kafkaObjectMapper() {
        return new ObjectMapper()
                .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
                .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Bean
    public NewTopic welcomeEmailTopic() {
        return TopicBuilder.name(esapProperties.getKafka().getTopics().getWelcomeEmail()).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic pushNotificationTopic() {
        return TopicBuilder.name(esapProperties.getKafka().getTopics().getPushNotification()).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic tokenRegistrationTopic() {
        return TopicBuilder.name(esapProperties.getKafka().getTopics().getTokenRegistration()).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic userProfileTopic() {
        return TopicBuilder.name(esapProperties.getKafka().getTopics().getUserProfile()).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic appointmentCreatedTopic() {
        return TopicBuilder.name(esapProperties.getKafka().getTopics().getAppointmentCreated()).partitions(3).replicas(1).build();
    }
}
