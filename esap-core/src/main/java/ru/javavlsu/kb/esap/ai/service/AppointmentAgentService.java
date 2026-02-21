package ru.javavlsu.kb.esap.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import ru.javavlsu.kb.esap.model.Patient;
import ru.javavlsu.kb.esap.service.PatientService;

import java.util.UUID;

/**
 * AppointmentAgentService 17.02.2026 Alexey Karabanov
 * Copyright (c) 2026 WINGS.
 */
@Slf4j
@Service
@SessionScope
public class AppointmentAgentService {
    private final ChatClient chatClient;
    private final PatientService patientService;
    private final String conversationId;

    public AppointmentAgentService(ChatClient chatClient, PatientService patientService) {
        this.chatClient = chatClient;
        this.patientService = patientService;
        this.conversationId = UUID.randomUUID().toString();
    }

    public String processMessage(String message, Long patientId) {
        log.info("Processing message: patientId={}, message='{}'", patientId, message);
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
}
