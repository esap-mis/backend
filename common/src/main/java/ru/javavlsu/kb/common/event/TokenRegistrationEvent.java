package ru.javavlsu.kb.common.event;

public record TokenRegistrationEvent(Long userId, String token) {
}
