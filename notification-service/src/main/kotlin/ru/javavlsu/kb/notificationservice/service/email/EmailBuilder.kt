package ru.javavlsu.kb.notificationservice.service.email

import org.springframework.stereotype.Component
import ru.javavlsu.kb.notificationservice.dto.PatientCreatedEvent

/**
 * EmailBuilder 24.07.2026 thewyolar
 * Copyright (c) 2026.
 */
@Component
class EmailBuilder {

    data class EmailContent(
        val to: String,
        val subject: String,
        val body: String
    )

    fun build(userData: PatientCreatedEvent): EmailContent {
        return EmailContent(
            to = userData.email,
            subject = "Добро пожаловать в нашу клинику ${userData.clinicName}!",
            body = buildBody(userData)
        )
    }

    private fun buildBody(userData: PatientCreatedEvent): String {
        return """
            Уважаемый ${userData.firstName}!
            Вы успешно зарегистрированы в поликлинике "${userData.clinicName}".
            
            Ваши данные для входа в личный кабинет:
            • Логин: ${userData.login}
            • Пароль: ${userData.password}
            
            Для безопасности рекомендуем сменить пароль после первого входа.
            С уважением,
            Команда поликлиники "${userData.clinicName}"
        """.trimIndent()
    }
}