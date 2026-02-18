package ru.javavlsu.kb.esap.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.javavlsu.kb.esap.ai.service.AppointmentAgentService;
import ru.javavlsu.kb.esap.dto.chat.ChatRequestDTO;
import ru.javavlsu.kb.esap.dto.chat.ChatResponseDTO;
import ru.javavlsu.kb.esap.model.Patient;
import ru.javavlsu.kb.esap.util.UserUtils;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/api/agent")
public class AgentController {
    private final AppointmentAgentService agentService;
    private final UserUtils userUtils;

    @Autowired
    public AgentController(AppointmentAgentService agentService, UserUtils userUtils) {
        this.agentService = agentService;
        this.userUtils = userUtils;
    }

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody ChatRequestDTO request) {
        log.info("Chat request: message='{}'", request.message());
        final Patient patient = (Patient) userUtils.UserDetails().getUser();
        final String response = agentService.processMessage(request.message(), patient.getId());
        return ResponseEntity.ok(new ChatResponseDTO(response));
    }
}
