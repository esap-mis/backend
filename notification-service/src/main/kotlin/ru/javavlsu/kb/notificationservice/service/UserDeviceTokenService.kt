package ru.javavlsu.kb.notificationservice.service

import org.springframework.stereotype.Service
import ru.javavlsu.kb.notificationservice.dto.TokenRegistrationEvent
import ru.javavlsu.kb.notificationservice.exception.UserDeviceTokenNotFoundException
import ru.javavlsu.kb.notificationservice.model.TokenStatus
import ru.javavlsu.kb.notificationservice.model.UserDeviceToken
import ru.javavlsu.kb.notificationservice.repository.UserDeviceTokenRepository

/**
 * UserDeviceTokenService 23.07.2026 thewyolar
 * Copyright (c) 2026.
 */
@Service
class UserDeviceTokenService(
    val userDeviceTokenRepository: UserDeviceTokenRepository
) {

    fun getUserDeviceTokensByUserId(userId: Long): List<UserDeviceToken> {
        return userDeviceTokenRepository.getUserDeviceTokensByUserId(userId)
    }

    fun saveToken(tokenRegistrationEvent: TokenRegistrationEvent) {
        if (!userDeviceTokenRepository.existsByTokenAndUserId(tokenRegistrationEvent.token,
                tokenRegistrationEvent.userId)) {
            val userDeviceToken = UserDeviceToken(userId = tokenRegistrationEvent.userId,
                token = tokenRegistrationEvent.token)
            userDeviceTokenRepository.save(userDeviceToken)
        }
    }

    fun disableToken(token: String) {
        val deviceToken = userDeviceTokenRepository.findUserDeviceTokenByToken(token)
            .orElseThrow { UserDeviceTokenNotFoundException("Token=$token not found") }
        deviceToken.status = TokenStatus.INACTIVE
        userDeviceTokenRepository.save(deviceToken)
    }
}