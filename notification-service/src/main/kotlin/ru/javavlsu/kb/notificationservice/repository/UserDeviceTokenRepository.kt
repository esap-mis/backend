package ru.javavlsu.kb.notificationservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.javavlsu.kb.notificationservice.model.UserDeviceToken
import java.util.Optional

/**
 * UserDeviceTokenRepository 23.07.2026 thewyolar
 * Copyright (c) 2026.
 */
interface UserDeviceTokenRepository : JpaRepository<UserDeviceToken, Long> {

    fun getUserDeviceTokensByUserId(userId: Long): List<UserDeviceToken>

    fun existsByTokenAndUserId(token: String, userId: Long): Boolean

    fun findUserDeviceTokenByToken(token: String): Optional<UserDeviceToken>
}
