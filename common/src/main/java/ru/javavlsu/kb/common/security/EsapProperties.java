package ru.javavlsu.kb.common.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Общие настройки безопасности и межсервисной коммуникации.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "esap")
public class EsapProperties {

    private final Jwt jwt = new Jwt();
    private final Cors cors = new Cors();
    private final Security security = new Security();
    private final Kafka kafka = new Kafka();
    private final Services services = new Services();

    @Getter
    @Setter
    public static class Jwt {
        private String secret = "secret";
        private String issuer = "ru.javavlsu.kb.esap";
        private Duration expiration = Duration.ofDays(30);
    }

    @Getter
    @Setter
    public static class Cors {
        private java.util.List<String> allowedOrigins = new java.util.ArrayList<>(java.util.List.of("http://localhost:3000"));
    }

    @Getter
    @Setter
    public static class Security {
        /**
         * Эндпоинты, доступные без аутентификации.
         */
        private java.util.List<String> permitAll = new java.util.ArrayList<>(java.util.List.of(
                "/actuator/health", "/actuator/health/**", "/error", "/api/chat/**"
        ));
    }

    @Getter
    @Setter
    public static class Kafka {
        private final Topics topics = new Topics();
    }

    @Getter
    @Setter
    public static class Topics {
        private String welcomeEmail = "user.registered.welcome-email";
        private String pushNotification = "user.notification.push.requested";
        private String tokenRegistration = "user.device.token.registered";
        private String userProfile = "esap.user.profile";
        private String appointmentCreated = "esap.appointment.created";
    }

    @Getter
    @Setter
    public static class Services {
        private String auth = "http://localhost:8081";
        private String clinic = "http://localhost:8082";
        private String schedule = "http://localhost:8083";
    }
}
