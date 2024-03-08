package ru.javavlsu.kb.esap.controller;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.javavlsu.kb.esap.dto.chat.ModelResponse;
import ru.javavlsu.kb.esap.service.ChatService;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ModelResponse> getAnswer(@RequestBody String message) {
        final Prompt prompt = new Prompt(new UserMessage(message));
        return ResponseEntity.ok(chatService.sendMessage(prompt));
    }
}
