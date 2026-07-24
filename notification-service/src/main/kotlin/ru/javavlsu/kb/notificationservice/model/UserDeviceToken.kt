package ru.javavlsu.kb.notificationservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field


/**
 * UserDeviceToken 23.07.2026 thewyolar
 * Copyright (c) 2026.
 */
@Document(collection = "user_device_tokens")
class UserDeviceToken (

    @Id
    val id: String? = null,

    @Field("fcm_token")
    @Indexed(unique = true)
    val token: String,

    @Field("user_id")
    @Indexed
    val userId: Long,

    @Field("status")
    var status: TokenStatus = TokenStatus.ACTIVE
)