package ru.javavlsu.kb.clinic;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import ru.javavlsu.kb.common.kafka.EventPublisher;
import ru.javavlsu.kb.common.security.AuthenticatedUser;
import ru.javavlsu.kb.common.security.JwtTokenProvider;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

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

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    protected EventPublisher eventPublisher;

    protected String bearerToken;

    @BeforeEach
    void beforeEach() {
        SecurityContextHolder.clearContext();
        bearerToken = jwtTokenProvider.generateToken(
                new AuthenticatedUser(10L, "doctor", Set.of("ROLE_DOCTOR"), 10L));
        mockMvc = webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();
    }
}
