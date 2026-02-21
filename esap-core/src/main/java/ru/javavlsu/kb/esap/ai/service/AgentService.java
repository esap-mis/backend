package ru.javavlsu.kb.esap.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.javavlsu.kb.esap.model.Patient;
import ru.javavlsu.kb.esap.service.PatientService;

/**
 * AgentService 17.02.2026 Alexey Karabanov
 * Copyright (c) 2026 WINGS.
 */
@Slf4j
@Service
public class AgentService {
    private final ChatClient chatClient;
    private final PatientService patientService;

    public AgentService(ChatClient chatClient, PatientService patientService) {
        this.chatClient = chatClient;
        this.patientService = patientService;
    }

    public String processMessage(String conversationId, String message, Long patientId) {
        log.info("Processing message: conversationId={}, patientId={}, message='{}'", conversationId, patientId, message);
        final String response = chatClient.prompt()
                .user(message)
                .advisors(advisor -> {
                    advisor.param(ChatMemory.CONVERSATION_ID, conversationId);
                    if (patientId != null) {
                        final Patient patient = patientService.getById(patientId);
                        advisor.param("patient_id", patientId);
                        advisor.param("patient_name", patient.getAddress());
                    }
                })
                .call()
                .content();

        log.info("Agent response: {}", response);
        return response;
    }

    public Flux<String> processMessageStream(String conversationId, String message, Long patientId) {
        log.info("Processing message: conversationId={}, patientId={}, message='{}'", conversationId, patientId, message);
        final Flux<String> response = chatClient.prompt()
                .user(message)
                .advisors(advisor -> {
                    advisor.param(ChatMemory.CONVERSATION_ID, conversationId);
                    if (patientId != null) {
                        final Patient patient = patientService.getById(patientId);
                        advisor.param("patient_id", patientId);
                        advisor.param("patient_name", patient.getAddress());
                    }
                })
                .stream()
                .content();

        log.info("Agent response: {}", response);
        return response;
    }
}
