package ru.javavlsu.kb.common.event;

public record NotificationEvent(Long userId, String title, String body) {
}
