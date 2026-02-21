package ru.javavlsu.kb.esap.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import ru.javavlsu.kb.esap.ai.service.AgentService;
import ru.javavlsu.kb.esap.dto.chat.ChatRequestDTO;
import ru.javavlsu.kb.esap.dto.chat.ChatResponseDTO;
import ru.javavlsu.kb.esap.dto.chat.MessageType;
import ru.javavlsu.kb.esap.model.Patient;
import ru.javavlsu.kb.esap.util.UserUtils;

import java.time.LocalDateTime;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/api/chat")
public class ChatController {
    private final AgentService agentService;
    private final UserUtils userUtils;

    @Autowired
    public ChatController(AgentService agentService, UserUtils userUtils) {
        this.agentService = agentService;
        this.userUtils = userUtils;
    }

    @PostMapping
    public ResponseEntity<?> chat(@RequestBody ChatRequestDTO request) {
        log.info("Chat request: message='{}'", request.message());
        final Patient patient = (Patient) userUtils.UserDetails().getUser();
        final String response = agentService.processMessage(request.sessionId(), request.message(), patient.getId());
        return ResponseEntity.ok(new ChatResponseDTO(
                request.sessionId(),
                response,
                MessageType.TEXT,
                LocalDateTime.now()
        ));
    }

    @PostMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponseDTO> streamChat(@RequestBody ChatRequestDTO request) {
        log.info("Chat request: message='{}'", request.message());
        final Patient patient = (Patient) userUtils.UserDetails().getUser();
        return agentService.processMessageStream(request.sessionId(), request.message(), patient.getId())
                .map(chunk -> new ChatResponseDTO(
                        request.sessionId(),
                        chunk,
                        MessageType.TEXT,
                        LocalDateTime.now()
                ))
                .doOnComplete(() -> log.info("Stream completed for session: {}", request.sessionId()));
    }
}
