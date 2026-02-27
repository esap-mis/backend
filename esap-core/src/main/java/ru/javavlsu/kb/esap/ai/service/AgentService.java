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
        return chatClient.prompt()
                .system(s -> {
                        if (patientId != null) {
                            final Patient patient = patientService.getById(patientId);
                            s.param("patient_id", patientId);
                            s.param("patient_full_name", patient.getFullName());
                        }
                    }
                )
                .user(message)
                .advisors(advisor -> {
                    advisor.param(ChatMemory.CONVERSATION_ID, conversationId);
                })
                .call()
                .content();
    }

    public Flux<String> processMessageStream(String conversationId, String message, Long patientId) {
        log.info("Processing message: conversationId={}, patientId={}, message='{}'", conversationId, patientId, message);
        return chatClient.prompt()
                .system(s -> {
                            if (patientId != null) {
                                final Patient patient = patientService.getById(patientId);
                                s.param("patient_id", patientId);
                                s.param("patient_full_name", patient.getFullName());
                            }
                        }
                )
                .user(message)
                .advisors(advisor -> {
                    advisor.param(ChatMemory.CONVERSATION_ID, conversationId);
                })
                .stream()
                .content();
    }
}
