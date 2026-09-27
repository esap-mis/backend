package ru.javavlsu.kb.core.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.javavlsu.kb.common.security.CurrentUser;
import ru.javavlsu.kb.core.client.EsapClient;
import ru.javavlsu.kb.core.dto.RemotePatientDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentService {

    private final ChatClient chatClient;
    private final EsapClient esapClient;
    private final CurrentUser currentUser;

    public String processMessage(String conversationId, String message, Long patientId) {
        return chat(conversationId, message, patientId).call().content();
    }

    public Flux<String> processMessageStream(String conversationId, String message, Long patientId) {
        return chat(conversationId, message, patientId).stream().content();
    }

    private ChatClient.ChatClientRequestSpec chat(String conversationId, String message, Long patientId) {
        log.info("Processing message: conversationId={}, patientId={}, message='{}'", conversationId, patientId, message);
        String pId = patientId != null ? patientId.toString() : "unknown";
        return chatClient.prompt()
                .system(s -> s.param("patient_id", pId).param("patient_full_name", resolveFullName(patientId)))
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId));
    }

    private String resolveFullName(Long patientId) {
        if (patientId == null) {
            return "unknown";
        }
        try {
            RemotePatientDTO patient = esapClient.getPatient(patientId);
            return patient == null ? "unknown" : patient.fullName();
        } catch (Exception e) {
            log.warn("Could not resolve patient full name for ID {}: {}", patientId, e.getMessage());
            return "unknown";
        }
    }
}
