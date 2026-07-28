package ru.javavlsu.kb.notificationservice.dto

/**
 * NotificationEvent 23.07.2026 thewyolar
 * Copyright (c) 2026.
 */
data class NotificationEvent(
    val userId: Long,
    val title: String,
    val body: String
)