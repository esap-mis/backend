package ru.javavlsu.kb.notificationservice.dto

/**
 * UserCreatedEvent 24.07.2026 thewyolar
 * Copyright (c) 2026.
 */
data class PatientCreatedEvent(
    val email: String,
    val firstName: String,
    val login: String,
    val password: String,
    val clinicName: String
)
