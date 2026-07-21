package ru.javavlsu.kb.esap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import ru.javavlsu.kb.esap.config.JWTFilter;
import ru.javavlsu.kb.esap.security.JWTUtil;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * AbstractControllerTest 21.07.2026 thewyolar
 * Copyright (c) 2026.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
@ExtendWith(MockitoExtension.class)
public abstract class AbstractControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;

    @Autowired
    private JWTFilter jwtFilter;

    @Autowired
    protected JWTUtil jwtUtil;

    @Autowired
    protected ObjectMapper objectMapper;

    protected String bearerToken;

    @BeforeEach
    void beforeEach() {
        bearerToken = jwtUtil.generateToken("admin");
        mockMvc = webAppContextSetup(wac)
                .addFilter(jwtFilter)
                .addFilter(((request, response, chain) -> {
                    request.setCharacterEncoding("UTF-8");
                    response.setCharacterEncoding("UTF-8");
                    chain.doFilter(request, response);
                }))
                .build();
    }
}
