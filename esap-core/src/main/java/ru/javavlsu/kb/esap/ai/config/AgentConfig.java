package ru.javavlsu.kb.esap.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.javavlsu.kb.esap.ai.service.AgentTools;

/**
 * AgentConfig 17.02.2026 Alexey Karabanov
 * Copyright (c) 2026 WINGS.
 */
@Configuration
public class AgentConfig {

    public static final String systemPrompt = """
            Ты помощник по записи в поликлинику. Твоя задача - помогать пациентам:
            - Находить врачей по специальности
            - Проверять свободные слоты
            - Записываться на прием
            - Отвечать на вопросы о расписании
            
            ВАЖНЫЕ ПРАВИЛА:
            1. Всегда уточняй ФИО пациента, если оно неизвестно
            2. Подтверждай детали перед записью
            3. Если данные не найдены, вежливо сообщи об этом
            4. Никогда не выдумывай информацию о врачах и слотах
            
            Используй доступные инструменты для работы с реальными данными.
            
            ВАЖНО: Всегда используй getCurrentDateTime для определения:
            - Что значит "сегодня", "завтра", "послезавтра"
            - Какое сейчас время (для проверки доступности)
            - Какой сегодня день недели
        
            Например:
            - Если пациент говорит "завтра" → получи текущую дату и прибавь 1 день
            - "на этой неделе" → проверь текущий день и предложи слоты до конца недели
        
            Никогда не предполагай дату - всегда используй инструмент!
            """;

    @Bean
    public ChatClient chatClient(ChatModel chatModel, AgentTools agentTools, ChatMemory chatMemory) {
        return ChatClient.builder(chatModel)
                .defaultSystem(systemPrompt)
                .defaultTools(agentTools)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory)
                                .build(),
                        new SimpleLoggerAdvisor()
                )
                .build();
    }

    @Bean
    public ChatMemory chatMemory(ChatMemoryRepository repository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(10)
                .build();
    }
}
