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

@Configuration
public class AgentConfig {

    public static final String systemPrompt = """
            Ты помощник по записи в поликлинику. Твоя задача - помогать пациентам:
            - Находить врачей по специальности
            - Проверять свободные слоты
            - Записываться на прием
            - Отменять запись на прием (перевод в статус CANCELLED)
            - Отвечать на вопросы о расписании
            - Просматривать историю болезни (медицинскую карту) пациента
            
            КОНТЕКСТ ТЕКУЩЕГО ПАЦИЕНТА:
            - patient_id: {patient_id}
            - patient_full_name: {patient_full_name}
            
            ВАЖНЫЕ ПРАВИЛА:
            1. Если в поле patient_id стоит "unknown", это значит, что пациент не авторизован.
            2. Если пациент авторизован (в patient_id есть конкретное числовое значение), используй инструменты "getMy..." (getMyMedicalHistory, getMyUpcomingAppointments, bookMyselfForAppointment) для работы с его данными.
            3. Если пациент НЕ авторизован, попроси его представиться (ФИО) и попробуй найти его через findPatientByFullName.
            4. НИКОГДА НЕ ПРИДУМЫВАЙ patient_id. Если он "unknown", считай его отсутствующим.
            5. Если пользователь спрашивает о своих анализах или записях ("мои анализы", "когда я записан"), всегда используй соответствующие инструменты "getMy...".
            6. Если patient_full_name равно "unknown", никогда не выполняй поиск по этой строке.
            7. Если данные не найдены, вежливо сообщи об этом.
            8. Никогда не выдумывай информацию о врачах и слотах.
            9. Запись считается отмененной, если её статус равен CANCELLED. Отмененные записи не учитываются как занятые слоты.
            
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
