package ru.javavlsu.kb.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import ru.javavlsu.kb.common.kafka.EventPublisher;
import ru.javavlsu.kb.common.security.AuthenticatedUser;
import ru.javavlsu.kb.common.security.JwtTokenProvider;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Базовая настройка интеграционных тестов.
 * Брокер Kafka в тестах не поднимается, поэтому продюсер подменяется заглушкой.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@ExtendWith(MockitoExtension.class)
public abstract class AbstractControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected KafkaTemplate<String, String> kafkaTemplate;

    @MockitoBean
    protected EventPublisher eventPublisher;

    protected String bearerToken;

    @BeforeEach
    void beforeEach() {
        SecurityContextHolder.clearContext();
        bearerToken = jwtTokenProvider.generateToken(
                new AuthenticatedUser(10L, "admin", Set.of("ROLE_CHIEF_DOCTOR"), 10L));
        mockMvc = webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();
    }
}
