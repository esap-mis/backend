package ru.javavlsu.kb.notificationservice.model

import jakarta.persistence.*

/**
 * UserDeviceToken 23.07.2026 thewyolar
 * Copyright (c) 2026.
 */
@Entity
@Table(name = "user_device_tokens")
class UserDeviceToken (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "fcm_token", nullable = false)
    val token: String,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: TokenStatus = TokenStatus.ACTIVE
)