package ru.javavlsu.kb.esap.controller;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/chat")
public class ChatController {
    private final OllamaChatClient chatClient;

    @Autowired
    public ChatController(OllamaChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping
    public Map generate(@RequestBody String message) {
        return Map.of("response", chatClient.call(message));
    }

    @GetMapping("/stream")
    public Flux<ChatResponse> generateStream(@RequestBody String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatClient.stream(prompt);
    }
}