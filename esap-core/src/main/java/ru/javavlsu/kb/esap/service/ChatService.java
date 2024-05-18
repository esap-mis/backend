package ru.javavlsu.kb.esap.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javavlsu.kb.esap.dto.chat.ModelResponse;

@Service
public class ChatService {
    private final OllamaChatClient chatClient;
    private final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    public ChatService(OllamaChatClient ollamaChatClient) {
        this.chatClient = ollamaChatClient;
    }

    public ModelResponse sendMessage(Prompt prompt) {
        final ChatResponse response = chatClient.call(prompt);
        logger.info("Get response {}", response.toString());
        return new ModelResponse(response.getResult().getOutput().getContent());
    }
}