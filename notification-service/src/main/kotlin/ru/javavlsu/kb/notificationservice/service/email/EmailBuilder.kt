package ru.javavlsu.kb.notificationservice.service.email

import org.springframework.stereotype.Component
import ru.javavlsu.kb.common.event.PatientCreatedEvent

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
            subject = "Р”РѕР±СЂРѕ РїРѕР¶Р°Р»РѕРІР°С‚СЊ РІ РЅР°С€Сѓ РєР»РёРЅРёРєСѓ ${userData.clinicName}!",
            body = buildBody(userData)
        )
    }

    private fun buildBody(userData: PatientCreatedEvent): String {
        return """
            РЈРІР°Р¶Р°РµРјС‹Р№ ${userData.firstName}!
            Р’С‹ СѓСЃРїРµС€РЅРѕ Р·Р°СЂРµРіРёСЃС‚СЂРёСЂРѕРІР°РЅС‹ РІ РїРѕР»РёРєР»РёРЅРёРєРµ "${userData.clinicName}".
            
            Р’Р°С€Рё РґР°РЅРЅС‹Рµ РґР»СЏ РІС…РѕРґР° РІ Р»РёС‡РЅС‹Р№ РєР°Р±РёРЅРµС‚:
            вЂў Р›РѕРіРёРЅ: ${userData.login}
            вЂў РџР°СЂРѕР»СЊ: ${userData.password}
            
            Р”Р»СЏ Р±РµР·РѕРїР°СЃРЅРѕСЃС‚Рё СЂРµРєРѕРјРµРЅРґСѓРµРј СЃРјРµРЅРёС‚СЊ РїР°СЂРѕР»СЊ РїРѕСЃР»Рµ РїРµСЂРІРѕРіРѕ РІС…РѕРґР°.
            РЎ СѓРІР°Р¶РµРЅРёРµРј,
            РљРѕРјР°РЅРґР° РїРѕР»РёРєР»РёРЅРёРєРё "${userData.clinicName}"
        """.trimIndent()
    }
}