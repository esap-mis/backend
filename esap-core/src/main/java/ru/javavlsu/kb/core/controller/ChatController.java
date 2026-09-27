package ru.javavlsu.kb.core.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import ru.javavlsu.kb.common.security.CurrentUser;
import ru.javavlsu.kb.core.ai.service.AgentService;
import ru.javavlsu.kb.core.dto.ChatRequestDTO;
import ru.javavlsu.kb.core.dto.ChatResponseDTO;
import ru.javavlsu.kb.core.dto.MessageType;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final AgentService agentService;
    private final CurrentUser currentUser;

    public ChatController(AgentService agentService, CurrentUser currentUser) {
        this.agentService = agentService;
        this.currentUser = currentUser;
    }

    @PostMapping
    public ResponseEntity<?> chat(@RequestBody ChatRequestDTO request) {
        log.info("Chat request: message='{}'", request.message());
        Long patientId = currentUser.isPatient() ? currentUser.id() : null;
        String response = agentService.processMessage(request.sessionId(), request.message(), patientId);
        return ResponseEntity.ok(new ChatResponseDTO(request.sessionId(), response, MessageType.TEXT, LocalDateTime.now()));
    }

    @PostMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponseDTO> streamChat(@RequestBody ChatRequestDTO request) {
        log.info("Chat request: message='{}'", request.message());
        Long patientId = currentUser.isPatient() ? currentUser.id() : null;
        return agentService.processMessageStream(request.sessionId(), request.message(), patientId)
                .map(chunk -> new ChatResponseDTO(request.sessionId(), chunk, MessageType.TEXT, LocalDateTime.now()))
                .doOnComplete(() -> log.info("Stream completed for session: {}", request.sessionId()));
    }
}
