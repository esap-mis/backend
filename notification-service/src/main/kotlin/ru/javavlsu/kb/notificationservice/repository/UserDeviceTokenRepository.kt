package ru.javavlsu.kb.notificationservice.repository

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import ru.javavlsu.kb.notificationservice.model.UserDeviceToken
import java.util.*

/**
 * UserDeviceTokenRepository 23.07.2026 thewyolar
 * Copyright (c) 2026.
 */
@Repository
interface UserDeviceTokenRepository : MongoRepository<UserDeviceToken, Long> {

    fun getUserDeviceTokensByUserId(userId: Long): List<UserDeviceToken>

    fun existsByTokenAndUserId(token: String, userId: Long): Boolean

    fun findUserDeviceTokenByToken(token: String): Optional<UserDeviceToken>
}
