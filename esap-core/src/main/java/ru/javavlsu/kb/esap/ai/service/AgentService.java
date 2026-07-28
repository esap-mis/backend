package ru.javavlsu.kb.esap.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.javavlsu.kb.esap.model.Patient;
import ru.javavlsu.kb.esap.service.PatientService;

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
        final String pId = patientId != null ? patientId.toString() : "unknown";
        String pFullNameRaw = "unknown";
        if (patientId != null) {
            try {
                pFullNameRaw = patientService.getById(patientId).getFullName();
            } catch (Exception e) {
                log.warn("Could not get patient full name for ID {}: {}", patientId, e.getMessage());
            }
        }
        final String pFullName = pFullNameRaw;
        return chatClient.prompt()
                .system(s -> s.param("patient_id", pId)
                        .param("patient_full_name", pFullName))
                .user(message)
                .advisors(advisor -> {
                    advisor.param(ChatMemory.CONVERSATION_ID, conversationId);
                })
                .call()
                .content();
    }

    public Flux<String> processMessageStream(String conversationId, String message, Long patientId) {
        log.info("Processing message: conversationId={}, patientId={}, message='{}'", conversationId, patientId, message);
        final String pId = patientId != null ? patientId.toString() : "unknown";
        String pFullNameRaw = "unknown";
        if (patientId != null) {
            try {
                pFullNameRaw = patientService.getById(patientId).getFullName();
            } catch (Exception e) {
                log.warn("Could not get patient full name for ID {}: {}", patientId, e.getMessage());
            }
        }
        final String pFullName = pFullNameRaw;
        return chatClient.prompt()
                .system(s -> s.param("patient_id", pId)
                        .param("patient_full_name", pFullName))
                .user(message)
                .advisors(advisor -> {
                    advisor.param(ChatMemory.CONVERSATION_ID, conversationId);
                })
                .stream()
                .content();
    }
}
