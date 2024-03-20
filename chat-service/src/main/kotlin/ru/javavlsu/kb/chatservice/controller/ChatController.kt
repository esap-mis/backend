package ru.javavlsu.kb.chatservice.controller

import org.springframework.ai.chat.ChatResponse
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.ollama.OllamaChatClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux

@RestController
@CrossOrigin
@RequestMapping("/api/chat")
class ChatController @Autowired constructor(
    private var chatClient: OllamaChatClient
) {

    @GetMapping
    fun generate(@RequestBody message: String?): Map<*, *> {
        return java.util.Map.of("response", chatClient.call(message))
    }

    @GetMapping("/stream")
    fun generateStream(@RequestBody message: String?): Flux<ChatResponse> {
        val prompt = Prompt(UserMessage(message))
        return chatClient.stream(prompt)
    }
}
