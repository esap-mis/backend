package ru.javavlsu.kb.esap.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.javavlsu.kb.esap.dto.chat.ModelResponse;
import ru.javavlsu.kb.esap.service.ChatService;

@RestController
@CrossOrigin
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;
    private final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ModelResponse> getAnswer(@RequestBody String message) {
        logger.info("Received message: {}", message);
        final Prompt prompt = new Prompt(new UserMessage(message));
        return ResponseEntity.ok(chatService.sendMessage(prompt));
    }
}
